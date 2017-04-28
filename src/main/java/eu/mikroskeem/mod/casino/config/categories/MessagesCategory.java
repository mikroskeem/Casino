package eu.mikroskeem.mod.casino.config.categories;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Arrays;
import java.util.List;

/**
 * @author Mark Vainomaa
 */
@ConfigSerializable
@Getter
public class MessagesCategory {
    @Setting
    private List<String> signLines = Arrays.asList(
            "",
            "^^^^^^^^^^^^^^^^",
            "Enter money amount, what",
            "you'd like to put in"
    );

    @Setting
    private String cancel = "§9Casino: §cCancelled the money put in request.";

    @Setting
    private String cancelWrongAmount = "§9Casino: §cCancelled the money put in request, " +
            "because the entered money amount is not a valid number.";

    @Setting
    private String cancelTooLittle = "§9Casino: §cCancelled the money put in request, " +
            "because the entered money is too low.";

    @Setting
    private String cancelTooBig = "§9Casino: §cCancelled the money put in request, " +
            "because the entered money is too big.";

    @Setting
    private String notEnoughMoney = "§cSorry, you do not have enough money";

    @Setting
    private String processing = "§9Casino: §bProcessing your money, please wait 10 seconds...";
}
