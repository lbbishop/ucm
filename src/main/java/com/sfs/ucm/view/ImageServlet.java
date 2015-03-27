package com.sfs.ucm.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sfs.ucm.model.Resource;

/**
 * The Image servlet for serving from database.
 * 
 * @author BalusC
 * @link http://balusc.blogspot.com/2007/04/imageservlet.html
 */
public class ImageServlet extends HttpServlet {

	// Constants ----------------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

	// Statics ------------------------------------------------------------------------------------

	@PersistenceContext
	private EntityManager em;

	// Actions ------------------------------------------------------------------------------------

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get ID from request.
		String imageId = request.getParameter("id");

		// Check if ID is supplied to the request.
		if (imageId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
			return;
		}

		// Lookup Image by ImageId in database.
		Long id = Long.valueOf(imageId);
		Resource resource = em.find(Resource.class,  id); 
		//em.createQuery("from Resource r where mr.id = :id", Resource.class).setParameter("id", id).getSingleResult();

		// Check if image is actually retrieved from database.
		if (resource == null) {
			// Do your thing if the image does not exist in database.
			// Throw an exception, or send 404, or show default/warning image, or just ignore it.
			response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
			return;
		}

		// Init servlet response.
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType(resource.getContentType());
		response.setHeader("Content-Length", String.valueOf(resource.getContents().length));
		response.setHeader("Content-Disposition", "inline; filename=\"" + resource.getName() + "\"");

		// Prepare streams.
		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		try {
			// Open streams
			input = new BufferedInputStream(new ByteArrayInputStream(resource.getContents()), DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			// Write file contents to response.
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		}
		finally {
			// Gently close streams.
			close(output);
			close(input);
		}
	}

	// Helpers (can be refactored to public utility class) ----------------------------------------

	private static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			}
			catch (IOException e) {
				// Do your thing with the exception. Print it, log it or mail it.
				e.printStackTrace();
			}
		}
	}

}
