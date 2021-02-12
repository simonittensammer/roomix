package at.htl.control;

import at.htl.entity.Message;
import at.htl.entity.Room;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class MessageRepository implements PanacheRepository<Message> { }
