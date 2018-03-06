import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.examples.feedforward.classification.PlotUtil;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import java.io.File;

/**
 * "Linear" Data Classification Example
 *
 * Based on the data from Jason Baldridge:
 * https://github.com/jasonbaldridge/try-tf/tree/master/simdata
 *
 * @author Josh Patterson
 * @author Alex Black (added plots)
 *
 */
public class MyProject {


    public static void main(String[] args) throws Exception {

        String trainFile = "/hsb_jsy_4.csv";
        String testFile = "/hsb_lyk_4.csv";
        String targetFile = "/object.csv";

        int seed = 123;
        //double learningRate = 0.01;
        double learningRate = 0.03;
        int batchSize = 50;
        int nEpochs = 30;


        int numInputs = 3;
        int numOutputs = 4;
        //int numInputs = 2;
        //int numOutputs = 2;
        int numHiddenNodes =30;
        //int numHiddenNodes = 20;

        final String filenameTrain  = new ClassPathResource(trainFile).getFile().getPath();
        final String filenameTest  = new ClassPathResource(testFile).getFile().getPath();
        //final String filenameTrain  = new ClassPathResource("/classification/linear_data_train.csv").getFile().getPath();
        //final String filenameTest  = new ClassPathResource("/classification/linear_data_eval.csv").getFile().getPath();


        //Load the training data:
        RecordReader rr = new CSVRecordReader();
//        rr.initialize(new FileSplit(new File("src/main/resources/classification/linear_data_train.csv")));
        rr.initialize(new FileSplit(new File(filenameTrain)));
        //원래 DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,2);
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,numOutputs);

        //Load the test/evaluation data:
        RecordReader rrTest = new CSVRecordReader();
        rrTest.initialize(new FileSplit(new File(filenameTest)));
        //DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,2);
        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,numOutputs);


        DataSetIterator result = new RecordReaderDataSetIterator(rrTest,batchSize,0,4);



        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(learningRate)
                .updater(Updater.NESTEROVS)     //To configure: .updater(new Nesterovs(0.9))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX).weightInit(WeightInit.XAVIER)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .pretrain(false).backprop(true).build();

    /*
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(learningRate)
                .updater(Updater.NESTEROVS)     //To configure: .updater(new Nesterovs(0.9))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes-1)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX).weightInit(WeightInit.XAVIER)
                        .build())
                .layer(2, new DenseLayer.Builder().nIn(numHiddenNodes-1).nOut(numHiddenNodes-3)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX).weightInit(WeightInit.XAVIER)
                        .build())
                .layer(3, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX).weightInit(WeightInit.XAVIER)
                        .nIn(numHiddenNodes-3).nOut(numOutputs).build())
                .pretrain(false).backprop(true).build();
        */



        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(10));  //Print score every 10 parameter updates



        for ( int n = 0; n < nEpochs; n++) {
            model.fit( trainIter );
        }

        System.out.println("Evaluate model....");
        Evaluation eval = new Evaluation(numOutputs);
        while(testIter.hasNext()){
            DataSet t = testIter.next();
            INDArray features = t.getFeatureMatrix();
            INDArray lables = t.getLabels();
            //여기서 feedforward 호출
            INDArray predicted = model.output(features,false);

            eval.eval(lables, predicted);

        }

        //Print the evaluation statistics
        System.out.println(eval.stats());


        //------------------------------------------------------------------------------------
        //Training is complete. Code that follows is for plotting the data & predictions only

        int nPointsPerAxis = 100;


        //Plot the data:
        double xMin = 0;
        double xMax = 1.0;
        //double yMin = -0.2;
        //double yMax = 0.8;
        double yMin = 0.0;
        double yMax = 1.0;
        double zMin = 0.0;
        double zMax = 1.0;

        //Let's evaluate the predictions at every point in the x/y input space
        //double[][] evalPoints = new double[nPointsPerAxis*nPointsPerAxis][2];
        double[][] evalPoints = new double[nPointsPerAxis*nPointsPerAxis][3];
        int count = 0;
        for( int i=0; i<nPointsPerAxis; i++ ){
            for( int j=0; j<nPointsPerAxis; j++ ){
               // for(int k =0; k<nPointsPerAxis;k++) {

                    double x = i * (xMax - xMin) / (nPointsPerAxis - 1) + xMin;
                    double y = j * (yMax - yMin) / (nPointsPerAxis - 1) + yMin;
                   // double z = k * (zMax - zMin) / (nPointsPerAxis - 1) + zMin;

                    evalPoints[count][0] = x;
                    evalPoints[count][1] = y;
                   // evalPoints[count][2] = z;

                    count++;
               // }
            }
        }


        INDArray allXYPoints = Nd4j.create(evalPoints);
        INDArray predictionsAtXYPoints = model.output(allXYPoints);


        //Get all of the training data in a single array, and plot it:
        //rr.initialize(new FileSplit(new ClassPathResource("/classification/linear_data_train.csv").getFile()));
        rr.initialize(new FileSplit(new ClassPathResource(trainFile).getFile()));
        rr.reset();
        //int nTrainPoints = 1000;
        int nTrainPoints = 10000;
        //trainIter = new RecordReaderDataSetIterator(rr,nTrainPoints,0,2);
        trainIter = new RecordReaderDataSetIterator(rr,nTrainPoints,0,numOutputs);


        DataSet ds = trainIter.next();
        PlotUtil2.plotTrainingData(ds.getFeatures(), ds.getLabels(), allXYPoints, predictionsAtXYPoints, nPointsPerAxis);
        //PlotUtil.plotTrainingData(ds.getFeatures(), ds.getLabels(), allXYoPints, predictionsAtXYPoints, nPointsPerAxis);

        //System.out.println(ds.getFeatures());


        //Get test data, run the test data through the network to generate predictions, and plot those predictions:
        //rrTest.initialize(new FileSplit(new ClassPathResource("/classification/linear_data_eval.csv").getFile()));
        rrTest.initialize(new FileSplit(new ClassPathResource(testFile).getFile()));
        rrTest.reset();
        int nTestPoints = 500;
        //testIter = new RecordReaderDataSetIterator(rrTest,nTestPoints,0,2);
        testIter = new RecordReaderDataSetIterator(rrTest,nTestPoints,0,numOutputs);
        ds = testIter.next();
        INDArray testPredicted = model.output(ds.getFeatures());
        PlotUtil2.plotTestData(ds.getFeatures(), ds.getLabels(), testPredicted, allXYPoints, predictionsAtXYPoints, nPointsPerAxis);
       // PlotUtil.plotTestData(데이터셋의 특성, 데이터셋의 라벨, 데이터셋의 특징을 모델에 넣었을 때 결과예측(라벨)값, 모든자료의 xy값, 모든자료의 xy값을 넣었을 때 결과값, 100);


        rrTest.initialize(new FileSplit(new ClassPathResource(targetFile).getFile()));
        //rrTest.reset();
        result = new RecordReaderDataSetIterator(rrTest,300,0,numOutputs);
        ds = result.next();
        INDArray Predicted = model.output(ds.getFeatures());

        System.out.println();
        System.out.println("확률 of   Spring  Summer  Fall  Winter");
        System.out.println("         " +Predicted);



        System.out.println("****************Example finished********************");
    }
}
