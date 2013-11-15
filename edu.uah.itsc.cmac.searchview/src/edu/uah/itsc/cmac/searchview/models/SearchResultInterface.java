/*
 * This interface is implemented by SearchResultView so as to accept search results from SearchView.
 */
package edu.uah.itsc.cmac.searchview.models;

import java.util.ArrayList;
public interface SearchResultInterface {
	public void accept(ArrayList<SearchResult> searchResults);
}
