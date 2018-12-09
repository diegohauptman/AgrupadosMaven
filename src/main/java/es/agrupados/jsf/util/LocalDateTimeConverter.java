/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.jsf.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.calendar.Calendar;

/**
 *
 * @author mundakamacbook
 */
@Named
@ApplicationScoped
@FacesConverter("localDateTimeConverter")
public class LocalDateTimeConverter implements Converter
{
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(extractPattern(component, context));
        try
        {
            return LocalDate.parse(value, formatter);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value)
    {
        if (value == null || (value instanceof String && StringUtils.isBlank((String) value)))
        {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(extractPattern(component, context));
        return formatter.format((LocalDate) value);
    }

    private String extractPattern(UIComponent component, FacesContext context)
    {
        // try to get infos from calendar component
        if (component instanceof Calendar)
        {
            Calendar calendarComponent = (Calendar) component;
            return calendarComponent.getPattern();
        }

        return null;
    }

}
