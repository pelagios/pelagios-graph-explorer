package org.pelagios.graph.exceptions;

public class DatasetNotFoundException extends Exception {

    private static final long serialVersionUID = 4568005232202481716L;

    private static final String DEFAULT_MESSAGE_TEMPLATE = "The dataset '@name@' was not found in the graph";

    public DatasetNotFoundException(String name) {
        super(DEFAULT_MESSAGE_TEMPLATE.replace("@name@", name));
    }

}
