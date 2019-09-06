# Reactive Books 1994—1999

### maeda STUDIO

> When I was just starting out in 1992 to create interactive, or reactive as I dubbed them, graphics, there was a great deal of CD-ROM-based content emerging that seemed to miss the point of computational media. With the digital media publisher Digitalogue, I created 4 books (the 5th never made it to print) that focused upon different aspects of the computer as related to the visual medium.
>
> The first was entitled [The Reactive Square](https://maedastudio.com/2004/rbooks2k/rsquare.html) released in 1994. The Reactive Square was 10 squares that respond to input from the microphone. After that came [Flying Letters](https://maedastudio.com/2004/rbooks2k/flyltr.html) in 1995 which used the mouse as input to manipulate typographic marionettes. I then created the series of 12 digital clocks in 1996 entitled, [12 o'clocks](https://maedastudio.com/2004/rbooks2k/twelve.html) that played upon simple graphical time-based behaviors. In 1998, I released [Tap, Type, Write](https://maedastudio.com/2004/rbooks2k/ttw.html) as an homage to the typewriter rendered in only black and white. To be released in 1999 was a piece called [Mirror Mirror](https://maedastudio.com/2004/rbooks2k/mirror.html) that used video input as the primary interaction means, but Digitalogue closed its doors in 2000 due to the founding publisher's illness.
>
> These books are no longer published, and the software exists only in Macintosh format (pre-OS X). A 10-minute video was created in 2002 that documents these pieces that you can now view [on Vimeo](https://vimeo.com/124707805).
>
> A multi-lingual promotional piece for the first 3 books is visible [here](http://maedastudio.com/rbooks/).
>
> Copyright 2005, John Maeda.

### Build
```
sbt
compile
fastOptJS
```

### Environment
```
cd assets
ln -s ../target/scala-2.12/reactive-books-fastopt.js reactive-books-fastopt.js
ln -s ../target/scala-2.12/reactive-books-fastopt.js.map reactive-books-fastopt.js.map
```

### Audio
Place any `audio.mp3` you like to `assets` directory.

### Run
```
cd assets
python -m SimpleHTTPServer 8000
```
Any other static HTTP server fit.

### Deploy
```
sbt
clean
fullOptJS

git checkout gh-pages
cp target/scala-2.12/reactive-books-opt.js app.js
git commit -m "deploy"
git push
```
