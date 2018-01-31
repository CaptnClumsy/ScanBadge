package com.clumsy.scanbadge.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

public class GoogleVisionApp {

	public static void main( String[] args )
    {
    	if (args.length!=1) {
    		System.err.println("Syntax: ScanBadgeApp <full path to image file>");
    		System.exit(1);
    	}
    	// Instantiates a client
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

          // The path to the image file to annotate
          String fileName = args[0];

          // Reads the image file into memory
          Path path = Paths.get(fileName);
          byte[] data = Files.readAllBytes(path);
          ByteString imgBytes = ByteString.copyFrom(data);

          // Builds the image annotation request
          List<AnnotateImageRequest> requests = new ArrayList<AnnotateImageRequest>();
          Image img = Image.newBuilder().setContent(imgBytes).build();
          Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
          AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
              .addFeatures(feat)
              .setImage(img)
              .build();
          requests.add(request);

          // Performs label detection on the image file
          BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
          List<AnnotateImageResponse> responses = response.getResponsesList();

          for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
              System.out.printf("Error: %s\n", res.getError().getMessage());
              return;
            }

            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
              annotation.getAllFields().forEach((k, v) ->
                  System.out.printf("%s : %s\n", k, v.toString()));
            }
          }
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
