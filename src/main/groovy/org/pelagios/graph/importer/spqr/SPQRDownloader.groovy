package org.pelagios.graph.importer.spqr

/**
 * A simple downloader for fetching data records from the SQPR site.
 * Warning: this fires A LOT of HTTP requests.
 * 
 * @author Rainer Simon
 */
class SPQRDownloader {
	
	static void main(String[] args) {
		String spqrBaseUrl = "http://www2.epcc.ed.ac.uk/~spqr/"
		String records = "src/test/resources/datasets/spqr/records.txt"
		String downloadDir = "src/test/resources/datasets/spqr/downloads"
		
		int ctr = 0;
		new File(records).eachLine() { line ->
			def text = new URL(spqrBaseUrl + line).text
			new File(downloadDir, line.replace('/', '_')).write(text, "UTF-8")
			ctr++;
		}
		
		println("downloaded ${ctr} files")
	}
	
}
