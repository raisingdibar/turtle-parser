(ns instaparse
  (:require [instaparse.core :as insta]
            [instaparse.combinators :as combinators]))

(def basic-grammars
  (combinators/ebnf "PERIOD = '.'
                     SPACE = #'\\s'
                     COLON = ':'"))

(def iri-grammars
  (combinators/ebnf
   "IRI = '<' [SCHEME COLON AUTHORITY] PATH PATHFRAGMENT* '>'
    SCHEME = #'\\w*'
    AUTHORITY = '//'
    PATH = #'\\w*.\\w*' PATHSEPARATOR?
    PATHSEPARATOR = ('/' | ':' | '#')?
    PATHFRAGMENT = (#'\\w' | '.' | '-' | '_' | '~')* PATHSEPARATOR?"))

(def prefix-grammars
  (combinators/ebnf
   "PREFIXLINE = PRETAG SPACE* PREFIX SPACE* IRI SPACE* PERIOD SPACE*
    PREFIXEDNAME = PREFIX LOCALNAME
    LOCALNAME = #'\\w*'
    PRETAG = #'@prefix'
    PREFIX = #'\\w*' COLON"))

(def triple-grammars
  (combinators/ebnf 
   "TRIPLE = (IRI|PREFIXEDNAME) SPACE* (IRI|PREFIXEDNAME) SPACE* (IRI|PREFIXEDNAME) SPACE* PERIOD SPACE*"))

(def turtle-grammars
  (combinators/ebnf
   "TURTLE = PREFIXLINE* TRIPLE*"))

(def triple
  (insta/parser
   (merge
    basic-grammars
    iri-grammars
    prefix-grammars
    triple-grammars)
   :start :TRIPLE))

(def prefixline
  (insta/parser
   (merge
    basic-grammars
    iri-grammars
    prefix-grammars)
   :start :PREFIXLINE))

(def prefixed-name
  (insta/parser
   (merge
    basic-grammars
    iri-grammars
    prefix-grammars)
   :start :PREFIXEDNAME))

(def turtle
  (insta/parser
   (merge
    basic-grammars
    iri-grammars
    prefix-grammars
    triple-grammars
    turtle-grammars)
   :start :TURTLE))

(comment
  (turtle
   "@prefix ex: <http://example.com>.\nex:Tyler foaf:likes ex:Melo.")

  (prefixline "@prefix foaf: <http://xmlns.com/foaf/0.1/> .")

  ;; This line is freezing.... perhaps its too ambiguous.
  (turtle (slurp "/Users/jtd/Developer/shout/tyler.ttl"))

  ;; (-> (slurp "/Users/jtd/Developer/shout/tyler.ttl") turtle)
  )