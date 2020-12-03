package ds.gae.servlets;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ds.gae.CarRentalModel;
import ds.gae.ReservationException;
import ds.gae.entities.Quote;
import ds.gae.entities.ReservationConstraints;
import ds.gae.view.JSPSite;
import ds.gae.view.Tools;

@SuppressWarnings("serial")
public class PersistTestServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(PersistTestServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String companyName = "Hertz";
        String email = "test.user@kuleuven.be";

        req.getSession().setAttribute("renter", email);

        try {

            if (CarRentalModel.get().getReservations(email).size() == 0) {

                ReservationConstraints c1 = new ReservationConstraints(Tools.DATE_FORMAT.parse("30.11.2020"),
                        Tools.DATE_FORMAT.parse("4.12.2020"), "Compact");
                //ReservationConstraints c2 = new ReservationConstraints(Tools.DATE_FORMAT.parse("30.11.2020"),
                        //Tools.DATE_FORMAT.parse("4.12.2020"), "Mini");
                //ReservationConstraints c3 = new ReservationConstraints(Tools.DATE_FORMAT.parse("30.11.2020"),
                        //Tools.DATE_FORMAT.parse("4.12.2020"), "Premium");

                final Quote q1 = CarRentalModel.get().createQuote(companyName, email, c1);
                //final Quote q2 = CarRentalModel.get().createQuote(companyName, email, c2);
                //final Quote q3 = CarRentalModel.get().createQuote(companyName, email, c3);
                
                //List<Quote> quotes = new ArrayList<>();
                //quotes.add(q1);
                //quotes.add(q2);
                //quotes.add(q3);
                
                CarRentalModel.get().confirmQuote(q1);
            }

            resp.sendRedirect(JSPSite.PERSIST_TEST.url());
        } catch (ParseException | ReservationException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
