package edu.wpi.fishfolk.controllers;

import static edu.wpi.fishfolk.controllers.AbsController.dbConnection;

import edu.wpi.fishfolk.database.TableEntry.SignagePreset;
import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class NewSignageController extends AbsController {
  @FXML Text textl0, textl1, textl2, textl3; // text for left room locations (0-3 is top-bottom)
  @FXML Text textr0, textr1, textr2, textr3; // text for right room locations (0-3 is top-bottom)
  @FXML
  Text subtextl0,
      subtextl1,
      subtextl2,
      subtextl3; // text for left room locations (0-3 is top-bottom)
  @FXML
  Text subtextr0,
      subtextr1,
      subtextr2,
      subtextr3; // text for right room locations (0-3 is top-bottom)
  @FXML
  ImageView iconl0, iconl1, iconl2, iconl3; // direction arrows for left side (0-3 is top-bottom)
  @FXML
  ImageView iconr0, iconr1, iconr2, iconr3; // direction arrows for right side (0-3 is top-bottom)

  @FXML MFXFilterComboBox<String> presetSelect; // choicebox for manually selecting signage preset

  String identifier = "TEST";
  ArrayList<Text> listTexts = new ArrayList<>();
  ArrayList<ImageView> listIcons = new ArrayList<>();
  ArrayList<Text> listSubText = new ArrayList<>();

  public void initialize() {

    // pulls all SignagePresets into an ArrayList to call in for loops
    ArrayList<SignagePreset> allPresets =
        (ArrayList<SignagePreset>) dbConnection.getAllEntries(TableEntryType.SIGNAGE_PRESET);

    // if the current date matches a date in one of the SignagePresets
    // the identifier is set to the name of that SignagePreset
    for (int i = 0; i < allPresets.size(); i++) {
      if (allPresets.get(i).getDate().equals(LocalDate.now()))
        identifier = allPresets.get(i).getName();
    }

    // sets identifier to presetSelect value only if a preset has been selected
    if (!(presetSelect.getValue() == null)) identifier = presetSelect.getValue();

    // loads relevant preset using previously defined identifier
    SignagePreset preset =
        (SignagePreset) dbConnection.getEntry(identifier, TableEntryType.SIGNAGE_PRESET);

    initTextList(); // loads all text boxes into ArrayList<Text> to call in for loops
    initIconsList(); // loads all arrow icons into ArrayList<ImageView> to call in for loops
    initSubtextList();

    // sets all of the signs based on the relevant preset
    for (int i = 0; i < 8; i++) {
      if (preset.getSigns()[i] == null) {
        listTexts.get(i).setOpacity(0); // if the i'th Sign in the current preset does not exist,
        listIcons.get(i).setOpacity(0); // make the i'th textbox and icon invisible
        listSubText.get(i).setOpacity(0);
      } else {
        listTexts.get(i).setOpacity(1);
        listIcons.get(i).setOpacity(1);
        listSubText.get(i).setOpacity(1); // CHANGE ONCE SUBTEXT IS ADDED TO DB
        listTexts
            .get(i)
            .setText(
                preset.getSigns()[i]
                    .getLabel()); // otherwise set the i'th text to the i'th Sign's name
        listIcons
            .get(i)
            .setRotate(
                preset.getSigns()[i]
                    .getDirection()); // and same for the i'th direction for the arrow
        listSubText.get(i).setText(preset.getSigns()[i].getSubtext());
      }
    }
    // spotless totally nuked the format of these functions huh

    // fills in presetSelect choice box with names of all SignagePresets
    presetSelect.getItems().clear();
    for (int i = 0; i < allPresets.size(); i++) {
      presetSelect.getItems().add(allPresets.get(i).getName());
    }

    // dictates what do do if preset is selected
    presetSelect.setOnAction(
        event -> {
          if (!(presetSelect.getValue() == null)) {
            for (int i = 0; i < allPresets.size(); i++) {
              if (presetSelect.getValue().equals(allPresets.get(i).getName())) {
                identifier = presetSelect.getValue();
                initialize();
                break;
              }
            }
          }
        });
  }

  // fills listTexts list for easier calling of text fields in for loops
  private void initTextList() {
    listTexts.add(textl0);
    listTexts.add(textl1);
    listTexts.add(textl2);
    listTexts.add(textl3);
    listTexts.add(textr0);
    listTexts.add(textr1);
    listTexts.add(textr2);
    listTexts.add(textr3);
  }

  // fills IconsList list for easier calling of direction arrows in for loops
  private void initIconsList() {
    listIcons.add(iconl0);
    listIcons.add(iconl1);
    listIcons.add(iconl2);
    listIcons.add(iconl3);
    listIcons.add(iconr0);
    listIcons.add(iconr1);
    listIcons.add(iconr2);
    listIcons.add(iconr3);
  }

  private void initSubtextList() {
    listSubText.add(subtextl0);
    listSubText.add(subtextl1);
    listSubText.add(subtextl2);
    listSubText.add(subtextl3);
    listSubText.add(subtextr0);
    listSubText.add(subtextr1);
    listSubText.add(subtextr2);
    listSubText.add(subtextr3);
  }
}
