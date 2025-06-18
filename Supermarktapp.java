package def;

	import javafx.application.Application;
	import javafx.collections.FXCollections;
	import javafx.collections.ObservableList;
	import javafx.geometry.Insets;
	import javafx.geometry.Pos;
	import javafx.scene.Scene;
	import javafx.scene.control.*;
	import javafx.scene.image.Image;
	import javafx.scene.image.ImageView;
	import javafx.scene.layout.*;
	import javafx.stage.Stage;
	import java.text.DecimalFormat;
	import javafx.util.Duration;
	import javafx.animation.PauseTransition;
	import java.util.*;
	import java.util.stream.Collectors;
	
	public class SupermarktApp extends Application {
	    private ImageView detailImageView;
	    private Label detailNameLabel;
	    private Label detailCountryLabel;
	    private Label detailPriceLabel;
	    private Label detailStockLabel;
	    private Label detailCitrusLabel;
	    private Button addToCartButton;
	    private Fruit selectedFruit;
	    private Map<Fruit, Integer> shoppingCartMap = new HashMap<>();
	    private VBox cartContentBox;
	    private Label totalPriceLabel;
	    private static final DecimalFormat df = new DecimalFormat("0.00");
	    private GridPane fruitGrid;
	    private ObservableList<Fruit> observableFruitList;
	
	    @Override
	    public void start(Stage primaryStage) {
	        primaryStage.setTitle("Supermarkt App");
	        List<Fruit> fruitList = FruitLoader.laadFruitVanJson("/def/vruchtenlijst_2.0.json"); 
	        fruitList.sort(Comparator.comparing(Fruit::isAanbieding, Comparator.reverseOrder())
	                .thenComparing(Fruit::getName, String.CASE_INSENSITIVE_ORDER));
	
	        BorderPane borderPane = new BorderPane();
	
	        // Filters
	        Set<String> countries = fruitList.stream()
	                .map(fruit -> fruit.getCountryOfOrigin().equals("Nederland (kassen)") ? "Nederland" : fruit.getCountryOfOrigin())
	                .collect(Collectors.toCollection(TreeSet::new));
	        countries.add("Alle");
	        ComboBox<String> countryFilter = new ComboBox<>(FXCollections.observableArrayList(countries));
	        countryFilter.setValue("Alle");
	
	        CheckBox citrusFilter = new CheckBox("Alleen citrusvruchten");
	        citrusFilter.setSelected(false);
	
	        CheckBox kleurFilter = new CheckBox("Toon beschikbaarheid met kleuren");
	        kleurFilter.setSelected(false);
	
	        TextField searchField = new TextField();
	        searchField.setPromptText("Zoek naam, land of categorie (bijv. citrus)");
	        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilteredGrid(fruitGrid, observableFruitList, countryFilter, kleurFilter, citrusFilter, searchField));
	
	        VBox filterPane = new VBox(10,
	                new Label("Filter vruchten:"),
	                new HBox(10, new Label("Land:"), countryFilter),
	                new HBox(10, new Label("Zoeken:"), searchField),
	                citrusFilter,
	                kleurFilter
	        );
	        filterPane.setPadding(new Insets(10));
	
	        // Initialize instance variables
	        fruitGrid = new GridPane();
	        fruitGrid.setPadding(new Insets(10));
	        fruitGrid.setHgap(10);
	        fruitGrid.setVgap(10);
	        fruitGrid.setAlignment(Pos.CENTER);
	
	        observableFruitList = FXCollections.observableArrayList(fruitList);
	        populateFruitGrid(fruitGrid, observableFruitList, kleurFilter.isSelected());
	
	        // Set event handlers
	        countryFilter.setOnAction(e -> updateFilteredGrid(fruitGrid, observableFruitList, countryFilter, kleurFilter, citrusFilter, searchField));
	        citrusFilter.setOnAction(e -> updateFilteredGrid(fruitGrid, observableFruitList, countryFilter, kleurFilter, citrusFilter, searchField));
	        kleurFilter.setOnAction(e -> updateFilteredGrid(fruitGrid, observableFruitList, countryFilter, kleurFilter, citrusFilter, searchField));
	
	        ScrollPane scrollPane = new ScrollPane(fruitGrid);
	        scrollPane.setFitToWidth(true);
	
	        VBox leftPane = new VBox(10, filterPane, scrollPane);
	        borderPane.setLeft(leftPane);
	
	        // Winkelmandje
	        cartContentBox = new VBox(10);
	        cartContentBox.setPadding(new Insets(10));
	
	        ScrollPane cartScroll = new ScrollPane(cartContentBox);
	        cartScroll.setFitToWidth(true);
	
	        totalPriceLabel = new Label("Totaal: €0.00");
	        Button confirmOrderButton = new Button("Bevestig Bestelling");
	        confirmOrderButton.setOnAction(e -> showConfirmationDialog());
	
	        VBox cartPane = new VBox(10, new Label("Winkelmand"), cartScroll, totalPriceLabel, confirmOrderButton);
	        cartPane.setPadding(new Insets(10));
	        cartPane.setPrefWidth(220);
	        borderPane.setRight(cartPane);
	
	        // Detailvenster
	        VBox detailPane = new VBox(15);
	        detailPane.setPadding(new Insets(20));
	
	        detailImageView = new ImageView();
	        detailImageView.setFitHeight(200);
	        detailImageView.setFitWidth(200);
	        detailImageView.setPreserveRatio(true);
	
	        detailNameLabel = new Label("Selecteer een vrucht uit de lijst");
	        detailNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
	
	        detailCountryLabel = new Label();
	        detailPriceLabel = new Label();
	        detailStockLabel = new Label();
	        detailCitrusLabel = new Label();
	
	        addToCartButton = new Button("Voeg toe aan winkelmand");
	        addToCartButton.setDisable(true);
	        addToCartButton.setOnAction(e -> addToCart());
	
	        detailPane.getChildren().addAll(
	                detailImageView,
	                detailNameLabel,
	                detailCountryLabel,
	                detailPriceLabel,
	                detailStockLabel,
	                detailCitrusLabel,
	                addToCartButton
	        );
	        borderPane.setCenter(detailPane);
	
	        Scene scene = new Scene(borderPane, 1100, 600);
	        primaryStage.setScene(scene);
	        primaryStage.show();
	    }
	
	    private void populateFruitGrid(GridPane fruitGrid, List<Fruit> fruits, boolean useColors) {
	        fruitGrid.getChildren().clear();
	        int columns = 4;
	        int index = 0;
	
	        for (int row = 0; row < Math.ceil(fruits.size() / 4.0); row++) {
	            for (int col = 0; col < columns; col++) {
	                if (index < fruits.size()) {
	                    Fruit fruit = fruits.get(index++);
	                    VBox fruitCell = new VBox(5);
	                    fruitCell.setAlignment(Pos.CENTER);
	                    fruitCell.setPadding(new Insets(5));
	
	                    StackPane imageContainer = new StackPane();
	                    ImageView imageView = new ImageView();
	                    imageView.setFitHeight(50);
	                    imageView.setFitWidth(50);
	                    imageView.setPreserveRatio(true);
	
	                    try {
	                        Image img = new Image(getClass().getResourceAsStream("/" + fruit.getImagePath()));
	                        imageView.setImage(img);
	                    } catch (Exception e) {
	                        imageView.setImage(null);
	                    }
	
	                    Label nameLabel = new Label(fruit.getName());
	                    nameLabel.setStyle("-fx-font-weight: bold;");
	
	                    String background = "#FFFFFF";
	                    String borderColor = "#D3D3D3";
	                    if (fruit.isAanbieding()) {
	                        borderColor = "#FFA500"; // Orange border for offerings
	                        if (useColors) {
	                            background = "#ADD8E6"; // Light blue for offerings
	                        }
	                    } else if (useColors) {
	                        int stock = fruit.getStock();
	                        if (stock == 0) {
	                            background = "#FF9999";
	                        } else if (stock > 50) {
	                            background = "#90EE90";
	                        } else {
	                            background = "#FFC107";
	                        }
	                    }
	
	                    if (fruit.getStock() == 0) {
	                        Label outOfStockLabel = new Label("X");
	                        outOfStockLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: red; -fx-font-weight: bold;");
	                        imageContainer.getChildren().addAll(imageView, outOfStockLabel);
	                    } else {
	                        imageContainer.getChildren().add(imageView);
	                    }
	
	                    fruitCell.setStyle("-fx-border-color: " + borderColor + "; -fx-border-width: 2; -fx-background-color: " + background);
	                    fruitCell.getChildren().addAll(imageContainer, nameLabel);
	
	                    fruitCell.setOnMouseClicked(e -> {
	                        selectedFruit = fruit;
	                        updateFruitDetails(fruit);
	                        addToCartButton.setDisable(fruit.getStock() == 0);
	                    });
	
	                    fruitGrid.add(fruitCell, col, row);
	                }
	            }
	        }
	    }
	
	    private void updateFruitDetails(Fruit fruit) {
	        detailNameLabel.setText(fruit.getName());
	        detailCountryLabel.setText("Herkomst: " + fruit.getCountryOfOrigin());
	        detailPriceLabel.setText("Prijs: €" + df.format(fruit.getPrice()));
	        detailStockLabel.setText("Voorraad: " + fruit.getStock());
	        detailCitrusLabel.setText("Citrus: " + (fruit.isCitrus() ? "Ja" : "Nee"));
	
	        try {
	            Image img = new Image(getClass().getResourceAsStream("/" + fruit.getImagePath()));
	            detailImageView.setImage(img);
	        } catch (Exception e) {
	            detailImageView.setImage(null);
	        }
	    }
	
	    private void updateCartDisplay() {
	        cartContentBox.getChildren().clear();
	        double total = 0.0;
	
	        if (shoppingCartMap.isEmpty()) {
	            cartContentBox.getChildren().add(new Label("Winkelmand is leeg"));
	        } else {
	            for (Fruit fruit : shoppingCartMap.keySet()) {
	                int count = shoppingCartMap.get(fruit);
	                double subtotal = count * fruit.getPrice();
	                total += subtotal;
	
	                HBox row = new HBox(10);
	                row.setAlignment(Pos.CENTER_LEFT);
	
	                Label nameLabel = new Label(count + " x " + fruit.getName() + " (€" + df.format(fruit.getPrice()) + ") = €" + df.format(subtotal));
	                Button plus = new Button("+");
	                Button minus = new Button("–");
	
	                plus.setOnAction(e -> {
	                    if (fruit.getStock() > shoppingCartMap.get(fruit)) {
	                        shoppingCartMap.put(fruit, shoppingCartMap.get(fruit) + 1);
	                        updateCartDisplay();
	                    } else {
	                        showAlert("Fout", "Niet genoeg voorraad voor " + fruit.getName(), Alert.AlertType.ERROR);
	                    }
	                });
	
	                minus.setOnAction(e -> {
	                    int current = shoppingCartMap.get(fruit);
	                    if (current > 1) {
	                        shoppingCartMap.put(fruit, current - 1);
	                    } else {
	                        shoppingCartMap.remove(fruit);
	                    }
	                    updateCartDisplay();
	                });
	
	                row.getChildren().addAll(nameLabel, plus, minus);
	                cartContentBox.getChildren().add(row);
	            }
	        }
	        totalPriceLabel.setText("Totaal: €" + df.format(total));
	    }
	
	    private void showAlert(String title, String content, Alert.AlertType alertType) {
	        Alert alert = new Alert(alertType);
	        alert.setTitle(title);
	        alert.setContentText(content);
	        alert.setHeaderText(null);
	        alert.show();
	        PauseTransition pause = new PauseTransition(Duration.seconds(2));
	        pause.setOnFinished(event -> alert.close());
	        pause.play();
	    }
	
	    private void addToCart() {
	        if (selectedFruit == null) {
	            showAlert("Fout", "Geen fruit geselecteerd", Alert.AlertType.ERROR);
	            return;
	        }
	        if (selectedFruit.getStock() == 0) {
	            showAlert("Fout", "Geen voorraad beschikbaar voor " + selectedFruit.getName(), Alert.AlertType.ERROR);
	            return;
	        }
	        shoppingCartMap.put(selectedFruit, shoppingCartMap.getOrDefault(selectedFruit, 0) + 1);
	        updateCartDisplay();
	        showAlert("Fruit Toegevoegd", selectedFruit.getName() + " toegevoegd aan winkelmandje", Alert.AlertType.INFORMATION);
	    }
	
	    private void showConfirmationDialog() {
	        if (shoppingCartMap.isEmpty()) {
	            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Winkelmand is leeg.");
	            alert.showAndWait();
	            return;
	        }
	
	        Dialog<ButtonType> dialog = new Dialog<>();
	        dialog.setTitle("Bevestiging Bestelling");
	
	        VBox content = new VBox(10);
	        content.setPadding(new Insets(10));
	        content.getChildren().add(new Label("Je hebt de volgende vruchten gekozen:"));
	
	        double total = 0.0;
	        for (Map.Entry<Fruit, Integer> entry : shoppingCartMap.entrySet()) {
	            Fruit fruit = entry.getKey();
	            int count = entry.getValue();
	            double subtotal = count * fruit.getPrice();
	            total += subtotal;
	
	            HBox row = new HBox(10);
	            ImageView imgView = new ImageView();
	            try {
	                imgView.setImage(new Image(getClass().getResourceAsStream("/" + fruit.getImagePath()), 30, 30, true, true));
	            } catch (Exception ignored) {
	            }
	
	            row.getChildren().addAll(imgView, new Label(count + " x " + fruit.getName() + " (€" + df.format(fruit.getPrice()) + ") = €" + df.format(subtotal)));
	            content.getChildren().add(row);
	        }
	
	        content.getChildren().add(new Label("Totaalbedrag: €" + df.format(total)));
	        content.getChildren().add(new Label("Bestellingsnummer: " + UUID.randomUUID().toString().substring(0, 8).toUpperCase()));
	        dialog.getDialogPane().setContent(content);
	        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
	        dialog.showAndWait();
	
	        shoppingCartMap.clear();
	        updateCartDisplay();
	    }
	
	    private void updateFilteredGrid(GridPane fruitGrid, ObservableList<Fruit> fullList,
	                                    ComboBox<String> countryFilter, CheckBox kleurFilter, CheckBox citrusFilter, TextField searchField) {
	        List<Fruit> filtered = new ArrayList<>(fullList);
	
	        if (!"Alle".equals(countryFilter.getValue())) {
	            String selected = countryFilter.getValue();
	            filtered = filtered.stream().filter(fruit -> {
	                String land = fruit.getCountryOfOrigin();
	                if (selected.equals("Nederland")) {
	                    return land.equals("Nederland") || land.equals("Nederland (kassen)");
	                }
	                return land.equals(selected);
	            }).collect(Collectors.toList());
	        }
	
	        if (citrusFilter.isSelected()) {
	            filtered = filtered.stream().filter(Fruit::isCitrus).collect(Collectors.toList());
	        }
	
	        String searchText = searchField.getText().toLowerCase();
	        if (!searchText.isEmpty()) {
	            filtered = filtered.stream().filter(fruit ->
	                fruit.getName().toLowerCase().contains(searchText) ||
	                fruit.getCountryOfOrigin().toLowerCase().contains(searchText) ||
	                (fruit.isCitrus() && "citrus".contains(searchText.toLowerCase()))
	            ).collect(Collectors.toList());
	        }
	
	        filtered.sort(Comparator.comparing(Fruit::isAanbieding, Comparator.reverseOrder())
	                .thenComparing(Fruit::getName, String.CASE_INSENSITIVE_ORDER));
	        populateFruitGrid(fruitGrid, filtered, kleurFilter.isSelected());
	    }
	
	    public static void main(String[] args) {
	        launch(args);
	    }
	}
