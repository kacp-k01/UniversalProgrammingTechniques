package org.example.Database;

import lombok.Getter;
import java.util.ListResourceBundle;

@Getter
public class Translation_en_GB extends ListResourceBundle {

    private final Object[][] contents = {
            {"lake", "jezioro"},
            {"sea", "morze"},
            {"mountains", "góry"}
    };
}