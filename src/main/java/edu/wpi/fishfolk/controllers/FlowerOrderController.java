package edu.wpi.fishfolk.controllers;

public class FlowerOrderController extends AbsController {
  /*

  @FXML AnchorPane itemPane;

  @FXML AnchorPane orderPane;

  @FXML ScrollPane itemWindow;

  @FXML MFXButton ClearButton;

  @FXML MFXButton SubmitButton;

  @FXML MFXButton springTab;

  @FXML MFXButton exoticTab;
  @FXML MFXButton saleTab;
  @FXML MFXButton favoriteTab;

  @FXML MFXFilterComboBox<String> locationPicker;

  @FXML MFXTextField nameBox;

  @FXML MFXTextField dateBox;

  int numflowers;

  String[] namesFlowers;

  double[] prices;

  int[] flowerAmounts;

  Text[] orderValues;

  MFXButton[] clearOrderButtons;

  MFXButton[] tabButtons;

  int numCartItems;

  int numSpring;
  int numExotic;
  int numsale;
  int numFavorite;

  @FXML
  private void initialize() {

    numSpring = 10;
    numExotic = 4;
    numsale = 6;
    numFavorite = 4;

    numCartItems = 0;
    numflowers = numSpring + numExotic + numFavorite + numFavorite;

    namesFlowers = new String[numflowers];
    prices = new double[numflowers];
    flowerAmounts = new int[numflowers];
    orderValues = new Text[numflowers];
    clearOrderButtons = new MFXButton[numflowers];

    tabButtons = new MFXButton[] {springTab, exoticTab, saleTab, favoriteTab};

    swapTab(1);

    springTab.setOnMouseClicked(
        event -> {
          swapTab(1);
        });

    exoticTab.setOnMouseClicked(
        event -> {
          swapTab(2);
        });

    saleTab.setOnMouseClicked(
        event -> {
          swapTab(3);
        });

    favoriteTab.setOnMouseClicked(
        event -> {
          swapTab(4);
        });

    ClearButton.setOnMouseClicked(
        event -> {
          clearAll();
        });

    // locationPicker.getItems().add("Room1");
    locationPicker.getItems().addAll(dbConnection.getDestLongnames());
  }

  private void addFlower(int numAdded, int numTotal) {
    numAdded = numAdded + 1;
    int yAdjust = 0;
    int xAdjust = 0;
    if (numAdded % 2 == 0) {
      yAdjust = -300;
      xAdjust = 450;
    }
    Group g = new Group();

    ImageView i = new ImageView();
    i.setImage(new Image(Fapp.class.getResourceAsStream("images/Flower1.png")));
    i.setY(-250 + (300 * numAdded) + yAdjust);
    i.setX(50 + xAdjust);
    i.setVisible(true);
    g.getChildren().add(i);

    Text flowerName = new Text();
    flowerName.setText(namesFlowers[numTotal]);
    flowerName.setY(150 + (300 * numAdded) + yAdjust);
    flowerName.setX(165 + xAdjust);
    flowerName.setFont(new Font("Courier", 30));
    flowerName.setTextAlignment(TextAlignment.CENTER);
    flowerName.setVisible(true);
    g.getChildren().add(flowerName);

    Text flowerPrice = new Text();
    flowerPrice.setText("$" + prices[numTotal]);
    flowerPrice.setY(190 + (300 * numAdded) + yAdjust);
    flowerPrice.setX(175 + xAdjust);
    flowerPrice.setFont(new Font("Courier", 30));
    flowerPrice.setTextAlignment(TextAlignment.CENTER);
    flowerPrice.setVisible(true);
    g.getChildren().add(flowerPrice);

    MFXButton addCartButton = new MFXButton();
    addCartButton.setLayoutX(145 + xAdjust);
    addCartButton.setLayoutY(210 + (300 * numAdded) + yAdjust);
    addCartButton.setMinHeight(50);
    addCartButton.setMinWidth(200);
    addCartButton.setStyle(ClearButton.getStyle());
    addCartButton.setTextFill(Color.WHITE);
    addCartButton.setText("Add to Cart");

    int flowerID = numTotal;
    addCartButton.setOnMouseClicked(
        event -> {
          addToCart(flowerID);
        });

    g.getChildren().add(addCartButton);

    itemPane.getChildren().add(g);

    SubmitButton.setOnMouseClicked(
        event -> {
          submit();
        });
  }

  public void addToCart(int itemNum) {

    flowerAmounts[itemNum] = flowerAmounts[itemNum] + 1;

    if (flowerAmounts[itemNum] == 1) {
      numCartItems++;
      orderPane.setMinHeight(40 + (50 * (numCartItems - 1)));
      ;
      Group g = new Group();

      Text cartItem = new Text();
      cartItem.setText(
          namesFlowers[itemNum]
              + "   "
              + flowerAmounts[itemNum]
              + "x   $"
              + (flowerAmounts[itemNum] * prices[itemNum]));
      cartItem.setY(30 + (50 * (numCartItems - 1)));
      cartItem.setX(10);
      cartItem.setFont(new Font("Courier", 20));
      cartItem.setTextAlignment(TextAlignment.CENTER);
      cartItem.setVisible(true);
      g.getChildren().add(cartItem);
      orderValues[itemNum] = cartItem;

      MFXButton removeButton = new MFXButton();
      removeButton.setLayoutX(300);
      removeButton.setLayoutY(5 + (50 * (numCartItems - 1)));
      removeButton.setMinHeight(30);
      removeButton.setMinWidth(100);
      removeButton.setStyle("-fx-background-color: red;");
      removeButton.setTextFill(Color.WHITE);
      removeButton.setText("Remove");
      g.getChildren().add(removeButton);
      clearOrderButtons[itemNum] = removeButton;

      orderPane.getChildren().add(g);

      int itemID = itemNum;
      removeButton.setOnMouseClicked(
          event -> {
            removeItem(itemID);
          });

    } else {
      orderValues[itemNum].setText(
          namesFlowers[itemNum]
              + "   "
              + flowerAmounts[itemNum]
              + "x   $"
              + (flowerAmounts[itemNum] * prices[itemNum]));
    }
  }

  private void removeItem(int itemID) {
    int passed = 0;
    flowerAmounts[itemID] = 0;
    numCartItems = numCartItems - 1;

    for (int items = 0; items < numflowers; items++) {
      if (!(flowerAmounts[items] == 0) && !(items == itemID)) {
        if (orderValues[items].getY() > orderValues[itemID].getY()) {
          clearOrderButtons[items].setLayoutY(orderValues[items].getY() - 75);
          orderValues[items].setY(orderValues[items].getY() - 50);
        }
      }
    }

    clearOrderButtons[itemID].setVisible(false);
    clearOrderButtons[itemID].setDisable(true);
    orderValues[itemID].setVisible(false);
  }

  private void swapTab(int tabNum) {

    itemWindow.setVvalue(0);

    for (int tab = 0; tab < 4; tab++) {
      tabButtons[tab].setStyle("-fx-background-color: #012d5a;");
      tabButtons[tab].setTextFill(Color.WHITE);
    }

    itemPane.getChildren().remove(0, itemPane.getChildren().size());

    if (tabNum == 1) {
      tabButtons[0].setStyle("-fx-background-color:  #f0Bf4c;");
      tabButtons[0].setTextFill(Color.BLACK);
      itemPane.setMinHeight(50 + (425 * ((1 + numSpring) / 2)));
      for (int i = 0; i < numSpring; i++) {
        prices[i] = 100.00;
        namesFlowers[i] = "Flower #" + (i + 1);
        addFlower(i, i);
      }
    }

    if (tabNum == 2) {
      tabButtons[1].setStyle("-fx-background-color:  #f0Bf4c;");
      tabButtons[1].setTextFill(Color.BLACK);
      itemPane.setMinHeight(50 + (425 * ((1 + numExotic) / 2)));
      for (int i = numSpring; i < numSpring + numExotic; i++) {
        prices[i] = 150.00;
        namesFlowers[i] = "Exotic Flower #" + (i + 1 - numSpring);
        addFlower(i - numSpring, i);
      }
    }

    if (tabNum == 3) {
      tabButtons[2].setStyle("-fx-background-color:  #f0Bf4c;");
      tabButtons[2].setTextFill(Color.BLACK);
      itemPane.setMinHeight(50 + (425 * ((1 + numsale) / 2)));
      for (int i = numSpring + numExotic; i < numSpring + numExotic + numsale; i++) {
        prices[i] = 50.00;
        namesFlowers[i] = "Cheap Flower #" + (i + 1 - (numSpring + numExotic));
        addFlower(i - (numSpring + numExotic), i);
      }
    }

    if (tabNum == 4) {
      tabButtons[3].setStyle("-fx-background-color:  #f0Bf4c;");
      tabButtons[3].setTextFill(Color.BLACK);
      itemPane.setMinHeight(50 + (425 * ((1 + numFavorite) / 2)));
      for (int i = numSpring + numExotic + numsale; i < numflowers; i++) {
        prices[i] = 100.00;
        namesFlowers[i] = "Favorite Flower #" + (i + 1 - (numSpring + numExotic + numsale));
        addFlower(i - (numSpring + numExotic + numsale), i);
      }
    }
  }

  private void clearAll() {
    for (int items = 0; items < numflowers; items++) {
      if (!(flowerAmounts[items] == 0)) {
        clearOrderButtons[items].setVisible(false);
        clearOrderButtons[items].setDisable(true);
        orderValues[items].setVisible(false);
        flowerAmounts[items] = 0;
      }
    }
    numCartItems = 0;
  }

  private void submit() {
    double totalPrice = 0;
    String name = nameBox.getText();
    LocalTime time = parseTime();

    String location = locationPicker.getValue();
    List<FlowerItem> items = new ArrayList<FlowerItem>();
    for (int item = 0; item < numflowers; item++) {
      if (!(flowerAmounts[item] == 0)) {
        items.add(new FlowerItem(namesFlowers[item], prices[item], flowerAmounts[item]));
        totalPrice = totalPrice + (prices[item] * flowerAmounts[item]);
      }
    }
    if (totalPrice == 0) {
      itemsError();
      return;
    }
    if (location == null) {
      roomError();
      return;
    }
    if (time == null) {
      timeError();
      return;
    }
    if (name.equals("")) {
      recipientError();
      return;
    }

    LocalDateTime deliveryTime = LocalDateTime.of(LocalDate.now(), time);
    if (deliveryTime.isBefore(LocalDateTime.now())) {
      deliveryTime.plusDays(1);
    }

    FlowerRequest flowerRequest =
        new FlowerRequest(
            "", FormStatus.submitted, "", name, location, deliveryTime, totalPrice, items);
    dbConnection.insertEntry(flowerRequest);

    Navigation.navigate(Screen.HOME);
  }

  private LocalTime parseTime() {
    String timeSel = dateBox.getText();
    int pos = timeSel.indexOf(":");
    int h = -1, m = -1;
    if (pos != -1) {
      h = Integer.parseInt(timeSel.substring(0, pos));
      if (timeSel.length() - pos >= 3) {
        m = Integer.parseInt(timeSel.substring(pos + 1, pos + 3));
      }
    }
    if (h == -1 || m == -1) {
      return null;
    }
    if (timeSel.toLowerCase().indexOf("pm") >= 0) {
      if (h != 12) {
        h += 12;
      }
    } else if (timeSel.toLowerCase().indexOf("am") >= 0) {
      if (h == 12) {
        h = 0;
      }
    }
    if (h > 23 || m >= 60) {
      return null;
    }
    return LocalTime.of(h, m);
  }

  /** Informs the user they have not input a valid time
  private void timeError() {
    submissionError("Please enter a valid time.");
  }

  /** informs the user they have not selected a room
  private void roomError() {
    submissionError("Please select a room.");
  }

  /** informs the user they have not selected any items
  private void itemsError() {
    submissionError("Please select at least one item.");
  }

  /** informs the user they have not specified the recipient of the order
  private void recipientError() {
    submissionError("Please enter a recipient.");
  }

  /**
   * pops up an error if the submission is invalid
   *
   * @param error the error message to display

  private void submissionError(String error) {
    PopOver popup = new PopOver();
    Text popText = new Text(error);
    popText.setFont(new Font("Open Sans Regular", 18));
    popup.setContentNode(popText);
    popup.setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);
    popup.show(SubmitButton);
  }

   */

}
