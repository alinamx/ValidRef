import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.csl.*;

import java.util.ArrayList;

public class MyItemProvider implements ItemDataProvider {

    InputReference input;
    CSLType type;

    public InputReference getInput() {
        return input;
    }

    public void setInput(InputReference input) {
        this.input = input;
    }

    public CSLType getType() {
        return type;
    }

    public void setType(CSLType type) {
        this.type = type;
    }

    @Override
    public CSLItemData retrieveItem(String id) {
        CSLItemDataBuilder builder = new CSLItemDataBuilder();
        if (type == null || input == null) {
            return null;
        }

        builder.id(id)
                .type(type)
                .title(input.getTitle())
                .author(addListOfNames(input.getAuthors()))
                .containerTitle(input.getJournalOrBooktitle())
                .editor(addListOfNames(input.getEditors()))
                .DOI(input.getDOI())
                .page(input.getPages().replace("--", "-"))
                .publisher(input.getPublisher())
                .publisherPlace(input.getLocation())
                .volume(input.getVolume())
                .issue(input.getNumber());
                //.note(input.getNote());
        if (!input.getYear().equals("")) {
            builder.issued(Integer.valueOf(input.getYear()));
        }
        //TODO add month and day
        return builder.build();
    }

    private CSLName[] addListOfNames(ArrayList persons) {
        ArrayList<CSLName> list = new ArrayList<>();
        for (Object person : persons) {
            String parts[] = person.toString().split(", ");
            if (parts.length > 1) {
                list.add(new CSLNameBuilder().given(parts[1]).family(parts[0]).build());
            } else {
                list.add(new CSLNameBuilder().given("").family(parts[0]).build());
            }
        }
        CSLName[] names = list.toArray(new CSLName[list.size()]);
        return names;
    }

    public String[] getIds() {
        String ids[] = {"ID-0"};
        return ids;
    }
}