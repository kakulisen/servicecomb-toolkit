package org.apache.servicecomb.toolkit.booking;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestSchema(schemaId = "bookingController")
public class BookingController {

  @PostMapping("/booking/movie/{name}/{movie}/{quantity}")
  public String movieTicket(@PathVariable String name, @PathVariable String movie, @PathVariable Integer quantity) {

    return String.format("%s booking %d  %s movie tickets", name, movie, quantity);
  }
}
