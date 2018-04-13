package ouhk.auction.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import ouhk.auction.model.Attachment;
import ouhk.auction.model.Item;
import ouhk.auction.view.Download;

@Controller
@RequestMapping("ticket")
public class ItemController {

    private volatile long TICKET_ID_SEQUENCE = 1;
    private Map<Long, Item> ticketDatabase = new Hashtable<>();

    @RequestMapping(value = {"", "list"}, method = RequestMethod.GET)
    public String list(ModelMap model) {
        model.addAttribute("ticketDatabase", ticketDatabase);
        return "itemlist";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create() {
        return new ModelAndView("additem", "ticketForm", new Form());
    }

    public static class Form {
        
        private int price;
        private int countdown;
        private String subject;
        private String body;
        private List<MultipartFile> attachments;

        public String getSubject() {
            return subject;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getCountdown() {
            return countdown;
        }

        public void setCountdown(int countdown) {
            this.countdown = countdown;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public List<MultipartFile> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<MultipartFile> attachments) {
            this.attachments = attachments;
        }

    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public View create(Form form, Principal principal) throws IOException {
        Item ticket = new Item();
        ticket.setId(this.getNextTicketId());
        ticket.setCustomerName(principal.getName());
        ticket.setSubject(form.getSubject());
        ticket.setBody(form.getBody());
        ticket.setCountdown(form.getCountdown());
        ticket.setPrice(form.getPrice());

        for (MultipartFile filePart : form.getAttachments()) {
            Attachment attachment = new Attachment();
            attachment.setName(filePart.getOriginalFilename());
            attachment.setMimeContentType(filePart.getContentType());
            attachment.setContents(filePart.getBytes());
            if (attachment.getName() != null && attachment.getName().length() > 0
                    && attachment.getContents() != null && attachment.getContents().length > 0) {
                ticket.addAttachment(attachment);
            }
        }
        this.ticketDatabase.put(ticket.getId(), ticket);
        return new RedirectView("/ticket/itemview/" + ticket.getId(), true);
    }

    private synchronized long getNextTicketId() {
        return this.TICKET_ID_SEQUENCE++;
    }

    @RequestMapping(value = "itemview/{ticketId}", method = RequestMethod.GET)
    public String view(@PathVariable("ticketId") long ticketId,
            ModelMap model) {
        Item ticket = this.ticketDatabase.get(ticketId);
        if (ticket == null) {
            return "redirect:/ticket/itemlist";
        }
        model.addAttribute("ticketId", Long.toString(ticketId));
        model.addAttribute("ticket", ticket);
        return "itemview";
    }

    @RequestMapping(
            value = "/{ticketId}/attachment/{attachment:.+}",
            method = RequestMethod.GET
    )
    public View download(@PathVariable("ticketId") long ticketId,
            @PathVariable("attachment") String name) {
        Item ticket = this.ticketDatabase.get(ticketId);
        if (ticket != null) {
            Attachment attachment = ticket.getAttachment(name);
            if (attachment != null) {
                return new Download (attachment.getName(),
                        attachment.getMimeContentType(), attachment.getContents());
            }
        }
        return new RedirectView("/ticket/itemlist", true);
    }

    @RequestMapping(
            value = "/{ticketId}/delete/{attachment:.+}",
            method = RequestMethod.GET
    )
    public String deleteAttachment(@PathVariable("ticketId") long ticketId,
            @PathVariable("attachment") String name) {
        Item ticket = this.ticketDatabase.get(ticketId);
        if (ticket != null) {
            if (ticket.hasAttachment(name)) {
                ticket.deleteAttachment(name);
            }
        }
        return "redirect:/ticket/editpage/" + ticketId;
    }

    @RequestMapping(value = "editpage/{ticketId}", method = RequestMethod.GET)
    public ModelAndView showEdit(@PathVariable("ticketId") long ticketId,
            Principal principal, HttpServletRequest request) {
        Item ticket = this.ticketDatabase.get(ticketId);
        if (ticket == null
                || (!request.isUserInRole("ROLE_ADMIN")
                && !principal.getName().equals(ticket.getCustomerName()))) {
            return new ModelAndView(new RedirectView("/ticket/itemlist", true));
        }
        ModelAndView modelAndView = new ModelAndView("editpage");
        modelAndView.addObject("ticketId", Long.toString(ticketId));
        modelAndView.addObject("ticket", ticket);

        Form ticketForm = new Form();
        ticketForm.setSubject(ticket.getSubject());
        ticketForm.setBody(ticket.getBody());
        modelAndView.addObject("ticketForm", ticketForm);

        return modelAndView;
    }

    @RequestMapping(value = "editpage/{ticketId}", method = RequestMethod.POST)
    public View edit(@PathVariable("ticketId") long ticketId, Form form,
            Principal principal, HttpServletRequest request)
            throws IOException {
        Item ticket = this.ticketDatabase.get(ticketId);
        if (ticket == null
                || (!request.isUserInRole("ROLE_ADMIN")
                && !principal.getName().equals(ticket.getCustomerName()))) {
            return new RedirectView("/ticket/itemlist", true);
        }
        
        ticket.setSubject(form.getSubject());
        ticket.setBody(form.getBody());

        for (MultipartFile filePart : form.getAttachments()) {
            Attachment attachment = new Attachment();
            attachment.setName(filePart.getOriginalFilename());
            attachment.setMimeContentType(filePart.getContentType());
            attachment.setContents(filePart.getBytes());
            if (attachment.getName() != null && attachment.getName().length() > 0
                    && attachment.getContents() != null && attachment.getContents().length > 0) {
                ticket.addAttachment(attachment);
            }
        }
        this.ticketDatabase.put(ticket.getId(), ticket);
        return new RedirectView("/ticket/itemview/" + ticket.getId(), true);
    }

    @RequestMapping(value = "delete/{ticketId}", method = RequestMethod.GET)
    public String deleteTicket(@PathVariable("ticketId") long ticketId) {
        if (this.ticketDatabase.containsKey(ticketId)) {
            this.ticketDatabase.remove(ticketId);
        }
        return "redirect:/ticket/itemlist";
    }

}
