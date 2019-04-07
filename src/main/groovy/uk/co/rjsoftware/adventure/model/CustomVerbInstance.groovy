package uk.co.rjsoftware.adventure.model

class CustomVerbInstance {

    private UUID id
    private String script

    CustomVerbInstance(UUID id) {
        this(id, "")
    }

    CustomVerbInstance(UUID id, String script) {
        this.id = id
        this.script = script
    }

    UUID getId() {
        this.id
    }

    void setId(UUID id) {
        this.id = id
    }

    String getScript() {
        this.script
    }

    void setScript(String script) {
        this.script = script
    }
}
