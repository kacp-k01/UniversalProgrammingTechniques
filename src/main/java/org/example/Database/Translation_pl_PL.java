package org.example.Database;

import lombok.Getter;
import java.util.ListResourceBundle;

@Getter
public class Translation_pl_PL extends ListResourceBundle {

    private final Object[][] contents = {
            {"jezioro", "lake"},
            {"morze", "sea"},
            {"góry", "mountains"}
    };
}
