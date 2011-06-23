package org.pelagios.importer.ptolemymachine;

public class PtolemyDataset {
		
	/*
	@Override
	public List<PlaceImpl> listPlaces() {
		NodeIterator it = model
			.listObjectsOfProperty(model.createProperty(OAC_NAMESPACE, OAC_HAS_BODY));

		List<PlaceImpl> places = new ArrayList<PlaceImpl>();
		while (it.hasNext()) {
			Resource resource = it.next().asResource();
			RDFNode source = resource.getProperty(DCTerms.source).getObject();

			try {
				places.add(resolver.getPlace(new URI(source.toString())));
			} catch (URISyntaxException e) {
				log.warn("Could not resolve URI. Might be a problem with the dataset. " +
						e.getMessage());			
			}
		}
		return places;
	}
	
	@Override
	public List<String> listTargets() {
		NodeIterator it = model
			.listObjectsOfProperty(model.createProperty(OAC_NAMESPACE, OAC_HAS_TARGET));
		
		List<String> targets = new ArrayList<String>();
		while (it.hasNext()) {
			Resource resource = it.next().asResource();
			RDFNode source = resource.getProperty(DCTerms.source).getObject();
			targets.add(source.toString());
		}
		return targets;
		
	}
	*/
	
}
