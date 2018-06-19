package com.javacreed.api.veclock;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

public class GenericVectorClockTest {

  @Test
  public void multipleClocks() {
    /* Based on the image shown in Wiki: https://en.wikipedia.org/wiki/Vector_clock#/media/File:Vector_Clock.svg */

    /* Using a custom mapFactory to simplify testing, as I use the toString method to verify the changes */
    final Supplier<Map<StringNode, LongVersion>> mapFactory = () -> new TreeMap<>();

    // @formatter:off
    GenericVectorClock<StringNode, LongVersion> a = GenericVectorClock.of(StringNode.of("a"), LongVersion.first(), mapFactory);
    GenericVectorClock<StringNode, LongVersion> b = GenericVectorClock.of(StringNode.of("b"), LongVersion.first(), mapFactory);
    GenericVectorClock<StringNode, LongVersion> c = GenericVectorClock.of(StringNode.of("c"), LongVersion.first(), mapFactory);
    // @formatter:on

    Assert.assertEquals("[a:0]", a.toString());
    Assert.assertEquals("[b:0]", b.toString());
    Assert.assertEquals("[c:0]", c.toString());

    /* C cause B */
    c = c.next();
    b = b.add(c);

    Assert.assertEquals("[a:0]", a.toString());
    Assert.assertEquals("[b:1,c:1]", b.toString());
    Assert.assertEquals("[c:1]", c.toString());

    /* B cause A */
    b = b.next();
    a = a.add(b);

    Assert.assertEquals("[a:1,b:2,c:1]", a.toString());
    Assert.assertEquals("[b:2,c:1]", b.toString());
    Assert.assertEquals("[c:1]", c.toString());

    /* B cause C */
    b = b.next();
    c = c.add(b);

    Assert.assertEquals("[a:1,b:2,c:1]", a.toString());
    Assert.assertEquals("[b:3,c:1]", b.toString());
    Assert.assertEquals("[b:3,c:2]", c.toString());

    /* A cause B */
    a = a.next();
    b = b.add(a);

    Assert.assertEquals("[a:2,b:2,c:1]", a.toString());
    Assert.assertEquals("[a:2,b:4,c:1]", b.toString());
    Assert.assertEquals("[b:3,c:2]", c.toString());

    /* C cause A */
    c = c.next();
    a = a.add(c);

    Assert.assertEquals("[a:3,b:3,c:3]", a.toString());
    Assert.assertEquals("[a:2,b:4,c:1]", b.toString());
    Assert.assertEquals("[b:3,c:3]", c.toString());

    /* B cause C */
    b = b.next();
    c = c.add(b);

    Assert.assertEquals("[a:3,b:3,c:3]", a.toString());
    Assert.assertEquals("[a:2,b:5,c:1]", b.toString());
    Assert.assertEquals("[a:2,b:5,c:4]", c.toString());

    /* C cause A */
    c = c.next();
    a = a.add(c);

    Assert.assertEquals("[a:4,b:5,c:5]", a.toString());
    Assert.assertEquals("[a:2,b:5,c:1]", b.toString());
    Assert.assertEquals("[a:2,b:5,c:5]", c.toString());
  }

  @Test
  public void oneClock() {
    final StringNode a = StringNode.of("a");
    final StringNode b = StringNode.of("b");
    final StringNode c = StringNode.of("c");

    GenericVectorClock<StringNode, LongVersion> clock = GenericVectorClock.of(a, LongVersion.first());
    Assert.assertEquals(1, clock.size());
    Assert.assertEquals(LongVersion.first(), clock.version());

    clock = clock.add(b, LongVersion.first());
    Assert.assertEquals(2, clock.size());
    Assert.assertEquals(LongVersion.of(1), clock.version());
    Assert.assertEquals(LongVersion.first(), clock.version(b).get());

    clock = clock.add(c, LongVersion.first());
    Assert.assertEquals(3, clock.size());
    Assert.assertEquals(LongVersion.of(2), clock.version());
    Assert.assertEquals(LongVersion.first(), clock.version(b).get());
    Assert.assertEquals(LongVersion.first(), clock.version(c).get());
  }

}
