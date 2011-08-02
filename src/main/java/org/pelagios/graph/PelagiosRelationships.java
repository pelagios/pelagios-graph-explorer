package org.pelagios.graph;

import org.neo4j.graphdb.RelationshipType;

/**
 * Relationships in the PELAGIOS graph. The Graph is organized as follows.
 * 
 *    [reference node] --- PLACES --> [place subref node] ---- PLACE --> [place]
 *           |                                 |-------------- PLACE --> [place]
 *           |                                 |-------------- PLACE --> [place] <------
 *        DATASETS                             .                                       |
 *           |                                 .                                       |
 *           |                                                                         |
 *  [dataset subref node] -- DATASET --> [dataset] <----- IS_SUBSET_OF -- [dataset]    |
 *                                                    |                      .         |
 *                                                    |                      .         |
 *                                                    |-- IS_SUBSET_OF -- [dataset]    |
 *                                                    |                      .         |
 *                                                    |                      .         |
 *                        [dataset] -- IS_SUBSET_OF ---                      .         |
 *                            |                                                        |
 *                            |                                                        |
 *                      GEOANNOTATION -----------> [geoannotation] -- REFERENCES -------
 *        
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public enum PelagiosRelationships implements RelationshipType {

    // The relation between the reference node and
    // all top-level data sets
    DATASETS, PLACES,

    // Dataset relations
    DATASET, IS_SUBSET_OF, GEOANNOTATION,

    // Place relations
    PLACE,

    // Relation between data record and place
    REFERENCES

}
