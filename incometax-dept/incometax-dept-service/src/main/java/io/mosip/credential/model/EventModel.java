package io.mosip.credential.model;
import lombok.Data;

@Data
public class EventModel {
    private String publisher;
    private String topic;
    private String publishedOn;
    private Event event;

}
