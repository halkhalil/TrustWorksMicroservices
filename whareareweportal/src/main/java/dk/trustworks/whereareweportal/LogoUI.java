package dk.trustworks.whereareweportal;

import com.vaadin.server.*;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by hans on 28/12/2016.
 */
public class LogoUI extends UI {
    public static final String IMAGE_URL = "myimage.png";

    private final RequestHandler requestHandler = new RequestHandler() {
        @Override
        public boolean handleRequest(VaadinSession session,
                                     VaadinRequest request, VaadinResponse response)
                throws IOException {
            if (("/" + IMAGE_URL).equals(request.getPathInfo())) {
                // Create an image, draw the "text" parameter to it and output
                // it to the browser.
                String text = request.getParameter("text");
                BufferedImage bi = new BufferedImage(100, 30,
                        BufferedImage.TYPE_3BYTE_BGR);
                bi.getGraphics().drawChars(text.toCharArray(), 0,
                        text.length(), 10, 20);
                response.setContentType("image/png");
                ImageIO.write(bi, "png", response.getOutputStream());

                return true;
            }
            // If the URL did not match our image URL, let the other request
            // handlers handle it
            return false;
        }
    };

    @Override
    public void init(VaadinRequest request) {
        Resource resource = new ExternalResource(IMAGE_URL + "?text=Hello!");

        getSession().addRequestHandler(requestHandler);

        // Add an image using the resource
        Image image = new Image("A dynamically generated image", resource);

        setContent(image);
    }

    @Override
    public void detach() {
        super.detach();

        // Clean up
        getSession().removeRequestHandler(requestHandler);
    }
}