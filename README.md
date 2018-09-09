# interactive-fiction-creator
tool for creating (and running) interactive fiction games.

Originally an exercise in learning Scala, rather than a serious attempt at creating
yet another framework for interactive fiction.  I decided to move the entire project to groovy.
The project had become difficult to work with, due to a mixture of Scala, Groovy (due to it's 
superior DSL and scripting support), and Java (due to Scala's lack of a decent enum).  This also meant
that getting it to build in gradle was going to require a significant refactor.

So, now it's all in groovy, and it builds in gradle :)

