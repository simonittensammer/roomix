package at.htl.boundary;

import at.htl.control.SongRepository;
import at.htl.control.YTSearchService;
import at.htl.entity.Song;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
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

    @Inject
    YTSearchService ytSearchService;

    @GET
    public List<Song> getAll() {
        return songRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Song getSong(@PathParam("id") Long id) {
        return songRepository.findById(id);
    }

    @GET
    @Path("/YT/search/{query}")
    public JsonObject searchYouTubeVideos(@PathParam("query") String query) {
        return ytSearchService.getVideos(query);
    }

    @GET
    @Path("/YT/duration/{videoId}")
    public JsonObject getYouTubeVideoDuration(@PathParam("videoId") String videoId) {
        return ytSearchService.getVideoDuration(videoId);
    }

    @POST
    public Response addSong(Song song) {
        if (songRepository.findByUrl(song.getUrl()) == null) {
            songRepository.persist(song);
            return Response.ok(song).build();
        }

        return Response.status(406).entity("song already exists").build();
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
