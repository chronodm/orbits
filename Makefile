
CLASSPATH = /usr/java/classes:.

WEBTEST = /ftp/entrenous/gr
WEBREL = /ftp/gravitation/orbits

SOURCE = Makefile fourmilab.Schwarzschild.java fourmilab.VisualFeedback.java bonk.au \
	 e.html energy.java energyPlot.java experiment.java \
	 orbitViewer.java

#OPTIMISE = -O
OPTIMISE = -g

all:	experiment.class fourmilab.VisualFeedback.class energyPlot.class energy.class \
	orbitViewer.class fourmilab.Schwarzschild.class
#	orbitPlot.class orbit.class

test:	all
	appletviewer e.html

clean:
	rm -f *.bak *.class

source:
	rm -f orbits.zip
	zip orbits.zip $(SOURCE)

webtest: all
	rm -rf $(WEBTEST)
	tar cfv /tmp/a.tar .
	mkdir $(WEBTEST)
	( cd $(WEBTEST) ; tar xfv /tmp/a.tar )

webrel: all
	rm -rf $(WEBREL)
	tar cfv /tmp/a.tar .
	mkdir $(WEBREL)
	( cd $(WEBREL) ; tar xfv /tmp/a.tar )

.java.class:
	javac $(OPTIMISE) -classpath $(CLASSPATH) $*.java

.SUFFIXES:  .java .class
