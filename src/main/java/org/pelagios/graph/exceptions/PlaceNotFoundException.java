package org.pelagios.graph.exceptions;

import java.net.URI;

public class PlaceNotFoundException extends Exception {

    private static final long serialVersionUID = -5589779150223550760L;

    private static final String DEFAULT_MESSAGE_TEMPLATE = "The place @uri@ was not found in the graph";

    public PlaceNotFoundException(URI uri) {
        super(DEFAULT_MESSAGE_TEMPLATE.replace("@uri@", uri.toString()));
    }

}
