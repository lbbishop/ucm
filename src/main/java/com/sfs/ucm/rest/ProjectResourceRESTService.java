package com.sfs.ucm.rest;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.sfs.ucm.model.Project;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the project table.
 */
@Path("/projects")
@RequestScoped
public class ProjectResourceRESTService implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PersistenceContext
   private EntityManager em;

   @GET
   @Produces("text/xml")
   public List<Project> listAllProjects() {
      // Use @SupressWarnings to force IDE to ignore warnings about "genericizing" the results of
      // this query
      @SuppressWarnings("unchecked")
      // We recommend centralizing inline queries such as this one into @NamedQuery annotations on
      // the @Entity class
      // as described in the named query blueprint:
      // https://blueprints.dev.java.net/bpcatalog/ee5/persistence/namedquery.html
      final List<Project> results = em.createQuery("select m from Project m order by m.name").getResultList();
      return results;
   }

   @GET
   @Path("/project/{id:[0-9][0-9]*}")
   @Produces("text/xml")
   public Project lookupProjectById(@PathParam("id") long id) {
      return em.find(Project.class, id);
   }
}
