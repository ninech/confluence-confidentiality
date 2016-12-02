#!/bin/sh

for SVG in *.svg
do
    name="${SVG%.*}"
    echo "$name.svg -> ${name}_[16x16|72x72].png"
    convert -density 1200 -resize 16x16 -background none $name.svg ${name}_16x16.png
    convert -density 1200 -resize 72x72 -background none $name.svg ${name}_72x72.png
done
