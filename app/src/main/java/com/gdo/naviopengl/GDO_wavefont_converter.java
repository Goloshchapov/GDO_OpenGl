import android.widget.Switch;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by gdjedi on 11/24/17.
 */

public class GDO_wavefont_converter {

    public static void main(String[] args){
        BufferedReader br = null;
        if(args != null && args[0].endsWith(".obj")){

            try {
                br = new BufferedReader(new FileReader(args[0]));
                getModelInfo(br);
                br.close();

            } catch (Exception e){
            } finally {

            }



        }

    }

    private static Model getModelInfo(BufferedReader br){
        Model model = new Model();
        try {
            String line;
            while((line = br.readLine()) !=null){
                String type = line.substring(0,2);
                switch (type){
                    case "v ":
                        model.positions++;
                        break;
                    case "f ":
                        model.faces++;
                        break;
                    case "vt":
                        model.texels++;
                        break;
                    case "vn":
                        model.normals++;
                        break;
                }
            }

            model.vertices = model.faces*3;


        } catch (Exception e){


        }
        return model;
    }


    private static class Model{
        int vertices = 0;
        int positions = 0;
        int texels = 0;
        int normals = 0;
        int faces = 0;
    }


}