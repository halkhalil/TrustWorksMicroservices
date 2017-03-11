package dk.trustworks.whereareweportal.images;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by hans on 28/12/2016.
 */
public class WorkIconCreator {

    public BufferedImage createWorkIcon(String path, String company, String... usernames) {
        System.out.println("WorkIconCreator.createWorkIcon");
        System.out.println("path = [" + path + "], company = [" + company + "], usernames = [" + usernames + "]");
        for (String username : usernames) {
            System.out.println("username = " + username);
        }

        //BufferedImage result = new BufferedImage(100, (50 + ((int)(Math.floor(usernames.length/4.0) + 1.0) * 38)), BufferedImage.TYPE_INT_RGB);
        BufferedImage result = new BufferedImage(200 + (usernames.length * 65), 100, BufferedImage.TYPE_INT_RGB);
        if(company.equals("trustworks")) {
            System.out.println("logo");
            //result = new BufferedImage(230, 200, BufferedImage.TYPE_INT_RGB);
            System.out.println("usernames.length = " + usernames.length);
            System.out.println("((int)(Math.floor(usernames.length/4.0)) * 100)) = " + ((int) (Math.floor(usernames.length / 4.0)) * 100));
            result = new BufferedImage(260, (271 + ((int)(Math.ceil(usernames.length/4.0)) * 100)), BufferedImage.TYPE_INT_RGB);
        }
        Graphics g = result.getGraphics();
        int x=0;
        int y=0;

        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new File(path + "/companies/" + company + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
            try {
                bi = ImageIO.read(new File(path + "/companies/trustworks.png"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        g.drawImage(bi, 0, 0, null);
        if(company.equals("trustworks")) {
            System.out.println("people");
            int column = 0;
            int row = 271;
            for (String username : usernames) {
                System.out.println("draw username = " + username);
                if (username.trim().equals("") || username.trim().equals("michala.christensen")) continue;
                //if (username.trim().equals("") || username.trim().equals("michael.bruun")) continue;
                //g.drawImage(ImageIO.read(new File(path + "/" + username +".png")), column, row, null);
                try {
                    g.drawImage(ImageIO.read(new File(path + "/employees/" + username + ".png")), column, row, null);
                } catch (IOException e) {
                    try {
                        g.drawImage(ImageIO.read(new File(path + "/employees/kommer-snart.jpg")), column, row, null);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                if(column < 195) {
                    column += 65;
                }
                else {
                    column = 0;
                    row += 100;
                }
            }

        } else {
            int column = 200;
            int row = 0;
            for (String username : usernames) {
                System.out.println("username = " + username);
                if (username.trim().equals("") || username.trim().equals("michala.christensen")) continue;
                //if (username.trim().equals("") || username.trim().equals("michael.bruun")) continue;
                try {
                    g.drawImage(ImageIO.read(new File(path + "/employees/" + username + ".png")), column, row, null);
                } catch (IOException e) {
                    try {
                        g.drawImage(ImageIO.read(new File(path + "/employees/kommer-snart.jpg")), column, row, null);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                column += 65;
            }
        }

        return result;
    }

}
