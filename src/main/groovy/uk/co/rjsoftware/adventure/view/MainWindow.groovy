package uk.co.rjsoftware.adventure.view

interface MainWindow {

    void say(String outputText)

    void addCommandListener(CommandListener listener)

    void addLoadListener(LoadListener listener)

    void loadAdventure(String title, String introduction)
}

interface LoadListener {
    void callback(LoadEvent event)
}

interface CommandListener {
    void callback(CommandEvent event)
}
