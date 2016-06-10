package dk.trustworks.personalassistant.nlp;

import dk.trustworks.personalassistant.model.ActionType;
import dk.trustworks.personalassistant.model.Training;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.*;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by hans on 20/05/16.
 */
public class IntentTrainer {

    private static IntentTrainer instance;

    private DocumentCategorizerME categorizer;
    private NameFinderME[] nameFinderMEs;

    private IntentTrainer() {
    }

    public void trainingByDB(DataSource ds) {
        Sql2o sql2o = new Sql2o(ds);
        try (Connection con = sql2o.open()) {
            List<Training> trainingList = con.createQuery("SELECT * FROM training;").executeAndFetch(Training.class);
            for (Training training : trainingList) {
                ActionType intent = training.action;
                //ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(trainingFile), "UTF-8");
                //ObjectStream<DocumentSample> documentSampleStream = new IntentDocumentSampleStream(intent, training.sentence);
                //categoryStreams.add(documentSampleStream);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void trainingByFiles() {
        try {
            File trainingDirectory = new File(ClassLoader.getSystemResource("training").toURI());
            if(!trainingDirectory.isDirectory()) {
                throw new IllegalArgumentException("TrainingDirectory is not a directory: " + trainingDirectory.getAbsolutePath());
            }

            List<ObjectStream<DocumentSample>> categoryStreams = new ArrayList<>();
            for (File trainingFile : trainingDirectory.listFiles()) {
                String intent = trainingFile.getName().replaceFirst("[.][^.]+$", "");
                ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(trainingFile), "UTF-8");
                ObjectStream<DocumentSample> documentSampleStream = new IntentDocumentSampleStream(intent, lineStream);
                categoryStreams.add(documentSampleStream);
            }
            ObjectStream<DocumentSample> combinedDocumentSampleStream = ObjectStreamUtils.createObjectStream(categoryStreams.toArray(new ObjectStream[0]));

            DoccatModel doccatModel = DocumentCategorizerME.train("en", combinedDocumentSampleStream, 0, 100);
            combinedDocumentSampleStream.close();

            List<TokenNameFinderModel> tokenNameFinderModels = new ArrayList<>();

            //for(String slot : slots) {
            //System.out.println("slot = " + slot);
            List<ObjectStream<NameSample>> nameStreams = new ArrayList<>();
            for (File trainingFile : trainingDirectory.listFiles()) {
                ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(trainingFile), "UTF-8");
                ObjectStream<NameSample> nameSampleStream = new NameSampleDataStream(lineStream);
                nameStreams.add(nameSampleStream);
            }
            ObjectStream<NameSample> combinedNameSampleStream = ObjectStreamUtils.createObjectStream(nameStreams.toArray(new ObjectStream[0]));

            TokenNameFinderModel tokenNameFinderModel = NameFinderME.train("en", "", combinedNameSampleStream, TrainingParameters.defaultParams(),
                    (AdaptiveFeatureGenerator)null, Collections.emptyMap());
            combinedNameSampleStream.close();
            tokenNameFinderModels.add(tokenNameFinderModel);
            //}

            categorizer = new DocumentCategorizerME(doccatModel);

            nameFinderMEs = new NameFinderME[tokenNameFinderModels.size()];
            for(int i = 0; i < tokenNameFinderModels.size(); i++) {
                nameFinderMEs[i] = new NameFinderME(tokenNameFinderModels.get(i));
            }

            System.out.println("Training complete. Ready.");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IntentOutcome process(String sentence) {
        double[] outcome = categorizer.categorize(sentence);
        IntentOutcome intentOutcome = new IntentOutcome(ActionType.valueOf(categorizer.getBestCategory(outcome)));
        //System.out.print("action=" + categorizer.getBestCategory(outcome) + " args={ ");

        String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(sentence);
        for (NameFinderME nameFinderME : nameFinderMEs) {
            Span[] spans = nameFinderME.find(tokens);

            for (int i = 0; i < spans.length; i++) {
                String[] names = Span.spansToStrings(spans, tokens);
                intentOutcome.args.put(spans[i].getType(), names[i]);
                //System.out.print(spans[i].getType() + "=" + names[i] + " ");
            }
        }
        return intentOutcome;
    }

    public static IntentTrainer getInstance() {
        if(instance==null) instance = new IntentTrainer();
        return instance;
    }

    public static void main(String[] args) throws Exception {

        String paragraph = "Hi. How are you? This is Mike.";

        // always start with a model, a model is learned from training data
        InputStream is = new FileInputStream(new File(ClassLoader.getSystemResource("en-sent.bin").toURI()));
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME sdetector = new SentenceDetectorME(model);

        String sentences[] = sdetector.sentDetect(paragraph);

        System.out.println(sentences[0]);
        System.out.println(sentences[1]);
        is.close();


        File trainingDirectory = new File(ClassLoader.getSystemResource(args[0]).toURI());
        String[] slots = new String[0];
        if(args.length > 1){
            slots = args[1].split(",");
        }

        if(!trainingDirectory.isDirectory()) {
            throw new IllegalArgumentException("TrainingDirectory is not a directory: " + trainingDirectory.getAbsolutePath());
        }

        List<ObjectStream<DocumentSample>> categoryStreams = new ArrayList<>();
        for (File trainingFile : trainingDirectory.listFiles()) {
            String intent = trainingFile.getName().replaceFirst("[.][^.]+$", "");
            ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(trainingFile), "UTF-8");
            ObjectStream<DocumentSample> documentSampleStream = new IntentDocumentSampleStream(intent, lineStream);
            categoryStreams.add(documentSampleStream);
        }
        ObjectStream<DocumentSample> combinedDocumentSampleStream = ObjectStreamUtils.createObjectStream(categoryStreams.toArray(new ObjectStream[0]));

        DoccatModel doccatModel = DocumentCategorizerME.train("en", combinedDocumentSampleStream, 0, 100);
        combinedDocumentSampleStream.close();

        List<TokenNameFinderModel> tokenNameFinderModels = new ArrayList<>();

        //for(String slot : slots) {
            //System.out.println("slot = " + slot);
            List<ObjectStream<NameSample>> nameStreams = new ArrayList<>();
            for (File trainingFile : trainingDirectory.listFiles()) {
                ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(trainingFile), "UTF-8");
                ObjectStream<NameSample> nameSampleStream = new NameSampleDataStream(lineStream);
                nameStreams.add(nameSampleStream);
            }
            ObjectStream<NameSample> combinedNameSampleStream = ObjectStreamUtils.createObjectStream(nameStreams.toArray(new ObjectStream[0]));

            TokenNameFinderModel tokenNameFinderModel = NameFinderME.train("en", "", combinedNameSampleStream, TrainingParameters.defaultParams(),
                    (AdaptiveFeatureGenerator)null, Collections.emptyMap());
            combinedNameSampleStream.close();
            tokenNameFinderModels.add(tokenNameFinderModel);
        //}

        DocumentCategorizerME categorizer = new DocumentCategorizerME(doccatModel);
        NameFinderME[] nameFinderMEs = new NameFinderME[tokenNameFinderModels.size()];
        for(int i = 0; i < tokenNameFinderModels.size(); i++) {
            nameFinderMEs[i] = new NameFinderME(tokenNameFinderModels.get(i));
        }

        System.out.println("Training complete. Ready.");
        System.out.print(">");
        String s;
        System.out.println("System.console() = " + System.console());
        Scanner scanner = new Scanner(System.in);

        while((s = scanner.nextLine()) != null){
            double[] outcome = categorizer.categorize(s);
            System.out.print("action=" + categorizer.getBestCategory(outcome) + " args={ ");

            String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(s);
            for (NameFinderME nameFinderME : nameFinderMEs) {
                Span[] spans = nameFinderME.find(tokens);

                for (int i = 0; i < spans.length; i++) {
                    String[] names = Span.spansToStrings(spans, tokens);
                    System.out.print(spans[i].getType() + "=" + names[i] + " ");
                }
            }
            System.out.println("}");
            System.out.print(">");
        }
    }
}
