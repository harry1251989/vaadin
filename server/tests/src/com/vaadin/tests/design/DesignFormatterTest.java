/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.design;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.declarative.DesignFormatter;

/**
 * Various tests related to formatter.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignFormatterTest {

    private DesignFormatter formatter;

    @Before
    public void setUp() {
        // initialise with default classes
        formatter = new DesignFormatter();
    }

    @Test
    public void testSupportedClasses() {

        for (Class<?> type : new Class<?>[] { boolean.class, char.class,
                byte.class, short.class, int.class, long.class, float.class,
                double.class, Boolean.class, Character.class, Byte.class,
                Short.class, Integer.class, Long.class, Float.class,
                Double.class, BigDecimal.class, String.class,
                ShortcutAction.class, Date.class, FileResource.class,
                ExternalResource.class, ThemeResource.class, Resource.class,
                TimeZone.class }) {
            assertTrue("not supported " + type.getSimpleName(),
                    formatter.canConvert(type));
        }
    }

    @Test
    public void testBoolean() {
        assertEquals("true", formatter.format(true));
        assertEquals("false", formatter.format(false));

        assertEquals(true, formatter.parse("true", boolean.class));
        assertEquals(true, formatter.parse("foobar", boolean.class));
        assertEquals(true, formatter.parse("", boolean.class));
        assertEquals(false, formatter.parse("false", boolean.class));

        assertEquals(true, formatter.parse("true", Boolean.class));
        assertEquals(true, formatter.parse("foobar", Boolean.class));
        assertEquals(true, formatter.parse("", Boolean.class));
        assertEquals(false, formatter.parse("false", Boolean.class));
    }

    @Test
    public void testIntegral() {
        byte b = 123;
        assertEquals("123", formatter.format(b));
        assertEquals(b, (byte) formatter.parse("123", byte.class));
        assertEquals((Byte) b, formatter.parse("123", Byte.class));

        b = -123;
        assertEquals("-123", formatter.format(b));
        assertEquals(b, (byte) formatter.parse("-123", byte.class));
        assertEquals((Byte) b, formatter.parse("-123", Byte.class));

        short s = 12345;
        assertEquals("12345", formatter.format(s));
        assertEquals(s, (short) formatter.parse("12345", short.class));
        assertEquals((Short) s, formatter.parse("12345", Short.class));

        s = -12345;
        assertEquals("-12345", formatter.format(s));
        assertEquals(s, (short) formatter.parse("-12345", short.class));
        assertEquals((Short) s, formatter.parse("-12345", Short.class));

        int i = 123456789;
        assertEquals("123456789", formatter.format(i));
        assertEquals(i, (int) formatter.parse("123456789", int.class));
        assertEquals((Integer) i, formatter.parse("123456789", Integer.class));

        i = -123456789;
        assertEquals("-123456789", formatter.format(i));
        assertEquals(i, (int) formatter.parse("-123456789", int.class));
        assertEquals((Integer) i, formatter.parse("-123456789", Integer.class));

        long l = 123456789123456789L;
        assertEquals("123456789123456789", formatter.format(l));
        assertEquals(l,
                (long) formatter.parse("123456789123456789", long.class));
        assertEquals((Long) l,
                formatter.parse("123456789123456789", Long.class));

        l = -123456789123456789L;
        assertEquals("-123456789123456789", formatter.format(l));
        assertEquals(l,
                (long) formatter.parse("-123456789123456789", long.class));
        assertEquals((Long) l,
                formatter.parse("-123456789123456789", Long.class));
    }

    @Test
    public void testFloatingPoint() {
        float f = 123.4567f;
        assertEquals("123.457", formatter.format(f));
        assertEquals(f, formatter.parse("123.4567", float.class), 1e-4);
        assertEquals(f, formatter.parse("123.4567", Float.class), 1e-4);

        double d = 123456789.123456789;
        assertEquals("123456789.123", formatter.format(d));
        assertEquals(d, formatter.parse("123456789.123456789", double.class),
                1e-9);
        assertEquals(d, formatter.parse("123456789.123456789", Double.class),
                1e-9);

    }

    @Test
    public void testBigDecimal() {
        BigDecimal bd = new BigDecimal("123456789123456789.123456789123456789");
        assertEquals("123456789123456789.123", formatter.format(bd));
        assertEquals(bd, formatter.parse(
                "123456789123456789.123456789123456789", BigDecimal.class));
    }

    @Test
    public void testChar() {
        char c = '\uABCD';
        assertEquals("\uABCD", formatter.format(c));
        assertEquals(c, (char) formatter.parse("\uABCD", char.class));
        assertEquals((Character) c, formatter.parse("\uABCD", Character.class));

        c = 'y';
        assertEquals(c, (char) formatter.parse("yes", char.class));
    }

    @Test
    public void testString() {

        for (String s : new String[] { "", "foobar", "\uABCD", "驯鹿" }) {
            assertEquals(s, formatter.format(s));
            assertEquals(s, formatter.parse(s, String.class));
        }
    }

    @Test
    public void testDate() throws Exception {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2012-02-17");
        String formatted = formatter.format(date);
        Date result = formatter.parse(formatted, Date.class);

        // writing will always give full date string
        assertEquals("2012-02-17 00:00:00+0200", formatted);
        assertEquals(date, result);

        // try short date as well
        result = formatter.parse("2012-02-17", Date.class);
        assertEquals(date, result);
    }

    @Test
    public void testShortcutActions() {
        ShortcutAction action = new ShortcutAction("&^d");
        String formatted = formatter.format(action);
        // note the space here - it separates key combination from caption
        assertEquals("alt-ctrl-d d", formatted);

        ShortcutAction result = formatter
                .parse(formatted, ShortcutAction.class);
        assertTrue(equals(action, result));
    }

    @Test
    public void testShortcutActionNoCaption() {
        ShortcutAction action = new ShortcutAction(null, KeyCode.D, new int[] {
                ModifierKey.ALT, ModifierKey.CTRL });
        String formatted = formatter.format(action);
        assertEquals("alt-ctrl-d", formatted);

        ShortcutAction result = formatter
                .parse(formatted, ShortcutAction.class);
        assertTrue(equals(action, result));
    }

    @Test
    public void testInvalidShortcutAction() {
        assertInvalidShortcut("-");
        assertInvalidShortcut("foo");
        assertInvalidShortcut("atl-ctrl");
        assertInvalidShortcut("-a");
    }

    protected void assertInvalidShortcut(String shortcut) {
        try {
            formatter.parse(shortcut, ShortcutAction.class);
            Assert.fail("Invalid shortcut '" + shortcut + "' should throw");
        } catch (ConversionException e) {
            // expected
        }
    }

    @Test
    public void testTimeZone() {
        TimeZone zone = TimeZone.getTimeZone("GMT+2");
        String formatted = formatter.format(zone);
        assertEquals("GMT+02:00", formatted);
        TimeZone result = formatter.parse(formatted, TimeZone.class);
        assertEquals(zone, result);
        // try shorthand notation as well
        result = formatter.parse("GMT+2", TimeZone.class);
        assertEquals(zone, result);
    }

    @Test
    public void testExternalResource() {
        String url = "://example.com/my%20icon.png?a=b";

        for (String scheme : new String[] { "http", "https", "ftp", "ftps" }) {
            Resource resource = formatter.parse(scheme + url, Resource.class);

            assertTrue(scheme + " url should be parsed as ExternalResource",
                    resource instanceof ExternalResource);
            assertEquals("parsed ExternalResource", scheme + url,
                    ((ExternalResource) resource).getURL());

            String formatted = formatter.format(new ExternalResource(scheme
                    + url));

            assertEquals("formatted ExternalResource", scheme + url, formatted);
        }
    }

    /**
     * A static method to allow comparison two different actions.
     * 
     * @param act
     *            One action to compare.
     * @param other
     *            Second action to compare.
     * @return <b>true</b> when both actions are the same (caption, icon, and
     *         key combination).
     */
    public static final boolean equals(ShortcutAction act, ShortcutAction other) {
        if (SharedUtil.equals(other.getCaption(), act.getCaption())
                && SharedUtil.equals(other.getIcon(), act.getIcon())
                && act.getKeyCode() == other.getKeyCode()
                && act.getModifiers().length == other.getModifiers().length) {
            HashSet<Integer> thisSet = new HashSet<Integer>(
                    act.getModifiers().length);
            // this is a bit tricky comparison, but there is no nice way of
            // making int[] into a Set
            for (int mod : act.getModifiers()) {
                thisSet.add(mod);
            }
            for (int mod : other.getModifiers()) {
                thisSet.remove(mod);
            }
            return thisSet.isEmpty();
        }
        return false;
    }

}
