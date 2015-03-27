package com.sfs.ucm.view;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class GalleriaManager {

	private List<String> images;

	@PostConstruct
	public void init() {
		images = new ArrayList<String>();

		for (int i = 1; i <= 8; i++) {
			images.add("process" + i + ".png");
		}
	}

	public List<String> getImages() {
		return images;
	}
}
