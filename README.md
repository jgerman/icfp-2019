# icfp-2019

This is my stab at the icfp contest 2019. (https://icfpcontest2019.github.io/)

I'd be happy to just get points on the board, and like other ICFP contests I'll probably come back to this down the road when I have more time. 

So far I've just parsed the task into a format I like. 

```clojure
icfp-2019.core> (task/read-task sample-task)
{:mine ({:x 0, :y 0} {:x 10, :y 0} {:x 10, :y 10} {:x 0, :y 10}),
:start {:x 0, :y 0},
:obstacles
(({:x 5, :y 8} {:x 6, :y 8} {:x 6, :y 9} {:x 5, :y 9})
({:x 4, :y 2} {:x 6, :y 2} {:x 6, :y 7} {:x 4, :y 7})),
:boosts
({:type "X", :point {:x 0, :y 9}}
{:type "L", :point {:x 0, :y 3}}
{:type "F", :point {:x 1, :y 2}}
{:type "F", :point {:x 0, :y 2}}
{:type "B", :point {:x 1, :y 1}}
{:type "B", :point {:x 0, :y 1}})}
icfp-2019.core>``` 

Using records might be overkill but it helps me think for now. 

# Usage

Added the initial problem files and a function to read a task from them, e.x.:

```clojure
(task/resource->task "part-1-intiial/prob-001.desc")```
