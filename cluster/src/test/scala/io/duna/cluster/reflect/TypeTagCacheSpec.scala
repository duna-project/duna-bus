package io.duna.cluster.reflect

import io.duna.reflect.TypeTagCache
import org.scalatest.{Assertions, FlatSpec}

/**
  * Created by eduribeiro on 19/06/2017.
  */
class TypeTagCacheSpec extends FlatSpec
  with Assertions {

  behavior of "TypeTagCache"

  it must "return no typetag when the name isn't a valid type expression" in {
    val genericType = "val randomExpression = 123"
    assertResult(None)(TypeTagCache.get(genericType))
  }
}
