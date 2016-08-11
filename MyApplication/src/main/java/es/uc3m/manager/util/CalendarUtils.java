package es.uc3m.manager.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.text.DateFormat;

public class CalendarUtils {

    public static long getCalendarMaxEventId(ContentResolver cr) {
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String[]{"MAX(_id) as max_id"}, null, null, "_id");
        assert cursor != null;
        cursor.moveToFirst();
        long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
        cursor.close();
        return max_val;
    }

    public static boolean isEvent(ContentResolver cr, Long eventId) {
        Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon();
        uri.appendPath(String.valueOf(eventId));
        Cursor query = null;
        try {
            query = cr.query(uri.build(), new String[]{CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND}, null, null, null);
            if (query != null && query.getCount() > 0 && query.moveToFirst()) {
                String name = query.getString(0);
                String date = DateFormat.getDateInstance(DateFormat.SHORT).format(Long.valueOf(query.getString(1)));
                if (name != null && !name.isEmpty() && date != null && !date.isEmpty())
                    return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (query != null)
                query.close();
        }

        return false;

    }


    /**
     * Add a new event into a native Google calendar. Add alert notification by setting <code>isRemind</code> as <code>true</code>.
     *
     * @param cr            - ContentResolver
     * @param title         - Event title
     * @param addInfo       - Event description
     * @param place         - Event place
     * @param status        -  <code>int</code> This information is sufficient for most entries: tentative (0), confirmed (1) or canceled (2):
     * @param startDate     - <code>long</code> event start time in mls
     * @param isRemind      - <code>boolean</code> need to remind about event before?
     * @param isMailService - <code>boolean</code>. Adding attendees to the meeting
     * @return <code>long</code> eventID
     */
    public static long addEventToCalender(ContentResolver cr, String title, String addInfo, String place, int status,
                                          long startDate, boolean isRemind, boolean isMailService) {
        String eventUriStr = "content://com.android.calendar/events";
        ContentValues event = new ContentValues();
        // id, We need to choose from our mobile for primary its 1
        event.put("calendar_id", 1);
        event.put("title", title);
        event.put("description", addInfo);
        event.put("eventLocation", place);
        event.put("eventTimezone", "UTC/GMT +2:00");

        // For next 1hr
        long endDate = startDate + 1000 * 60 * 60;
        event.put("dtstart", startDate);
        event.put("dtend", endDate);
        //If it is bithday alarm or such kind (which should remind me for whole day) 0 for false, 1 for true
        // values.put("allDay", 1);
        event.put("eventStatus", status);
        event.put("hasAlarm", 1);

        Uri eventUri = cr.insert(Uri.parse(eventUriStr), event);
        long eventID = Long.parseLong(eventUri != null ? eventUri.getLastPathSegment() : null);

        if (isRemind) {
            String reminderUriString = "content://com.android.calendar/reminders";
            ContentValues reminderValues = new ContentValues();
            reminderValues.put("event_id", eventID);
            // Default value of the system. Minutes is a integer
            reminderValues.put("minutes", 5);
            // Alert Methods: Default(0), Alert(1), Email(2), SMS(3)
            reminderValues.put("method", 1);
            cr.insert(Uri.parse(reminderUriString), reminderValues); //Uri reminderUri =
        }
        if (isMailService) {
            String attendeuesesUriString = "content://com.android.calendar/attendees";
            /********* To add multiple attendees need to insert ContentValues multiple times ***********/
            ContentValues attendeesValues = new ContentValues();
            attendeesValues.put("event_id", eventID);
            // Attendees name
            attendeesValues.put("attendeeName", "xxxxx");
            // Attendee email
            attendeesValues.put("attendeeEmail", "yyyy@gmail.com");
            // Relationship_Attendee(1), Relationship_None(0), Organizer(2), Performer(3), Speaker(4)
            attendeesValues.put("attendeeRelationship", 0);
            // None(0), Optional(1), Required(2), Resource(3)
            attendeesValues.put("attendeeType", 0);
            // None(0), Accepted(1), Decline(2), Invited(3), Tentative(4)
            attendeesValues.put("attendeeStatus", 0);
            cr.insert(Uri.parse(attendeuesesUriString), attendeesValues); //Uri attendeuesesUri =
        }

        return eventID;
    }

}