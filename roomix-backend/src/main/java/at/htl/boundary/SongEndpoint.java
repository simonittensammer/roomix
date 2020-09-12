package at.htl.boundary;

import at.htl.control.SongRepository;
import at.htl.entity.Song;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("song")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class SongEndpoint {

    @Inject
    SongRepository songRepository;

    @GET
    public List<Song> getAll() {
        return songRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Song getSong(@PathParam("id") Long id) {
        return songRepository.findById(id);
    }

    @POST
    public Response addSong(Song song) {
        songRepository.persist(song);
        return Response.ok(song).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSong(@PathParam("id") Long id) {
        Song song = songRepository.findById(id);

        if (song != null) {
            songRepository.delete(song);
            return Response.ok(song).build();
        }

        return Response.status(406).entity("song does not exist").build();
    }
}
