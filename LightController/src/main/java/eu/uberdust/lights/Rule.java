package eu.uberdust.lights;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 9/12/12
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Rule {

    private String source;
    private String sourceCapability;
    private String destination;
    private String destinationCapability;

    public Rule(String source, String sourceCapability, String destination, String destinationCapability) {
        this.source = source;
        this.sourceCapability = sourceCapability;
        this.destination = destination;
        this.destinationCapability = destinationCapability;
    }

    public Rule(String ruleStr) {
        this.source = ruleStr.split(" ")[0];
        this.sourceCapability = ruleStr.split(" ")[1];
        this.destination = ruleStr.split(" ")[2];
        this.destinationCapability = ruleStr.split(" ")[3];
    }

    public String getSource() {
        return source;
    }

    public String getSourceCapability() {
        return sourceCapability;
    }

    public String getDestination() {
        return destination;
    }

    public String getDestinationCapability() {
        return destinationCapability;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", sourceCapability='" + sourceCapability + '\'' +
                ", destinationCapability='" + destinationCapability + '\'' +
                '}';
    }
}
