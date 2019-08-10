module eru.gps.converter {
    requires com.google.guice;
    requires slf4j.api;
    requires org.apache.commons.lang3;
    requires jsr305;
    requires javax.inject;
    requires org.apache.logging.log4j;
    requires com.google.common;

    exports at.or.eru.gps.converter to com.google.guice;
}