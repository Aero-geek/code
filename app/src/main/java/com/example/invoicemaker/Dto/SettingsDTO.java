package com.example.invoicemaker.Dto;

public class SettingsDTO {
    public static SettingsDTO settingsDTO;
    private int currencyFormat;
    private int dateFormat;
    private long id;

    public static SettingsDTO getSettingsDTO() {
        return settingsDTO;
    }

    public static void setSettingsDTO(SettingsDTO settingsDTOe) {
        settingsDTO = settingsDTOe;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public int getCurrencyFormat() {
        return this.currencyFormat;
    }

    public void setCurrencyFormat(int i) {
        this.currencyFormat = i;
    }

    public int getDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(int i) {
        this.dateFormat = i;
    }
}
