package dk.trustworks.personalassistant.nlp;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.*;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by hans on 20/05/16.
 */
public class IntentTrainer {

    public static void main(String[] args) throws Exception {

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
        //while((s = System.console().readLine()) != null){
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
