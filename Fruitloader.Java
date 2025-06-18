package def;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FruitLoader {
    public static List<Fruit> laadFruitVanJson(String bestandspad) {
        List<Fruit> fruitLijst = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try {
            // Temporary workaround: Use file system path
            InputStream input = new FileInputStream("src/def/vruchtenlijst_2.0.json");
            InputStreamReader reader = new InputStreamReader(input);
            JSONObject root = (JSONObject) parser.parse(reader);
            JSONArray fruitArray = (JSONArray) root.get("vruchten");

            for (Object obj : fruitArray) {
                JSONObject fruitJson = (JSONObject) obj;

                String naam = (String) fruitJson.get("naam");
                String herkomst = (String) fruitJson.get("herkomst");
                String imagePath = (String) fruitJson.get("imagePath");
                double prijs = ((Number) fruitJson.get("prijs")).doubleValue();
                int voorraad = ((Number) fruitJson.get("voorraad")).intValue();
                boolean isCitrus = (boolean) fruitJson.get("isCitrus");
                boolean aanbieding = fruitJson.containsKey("aanbieding") && (boolean) fruitJson.get("aanbieding");
                String categorie = fruitJson.containsKey("categorie") ? (String) fruitJson.get("categorie") : "";

                Fruit fruit = new Fruit(naam, herkomst, imagePath, prijs, voorraad, isCitrus, aanbieding, categorie);
                fruitLijst.add(fruit);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fruitLijst;
    }
}
