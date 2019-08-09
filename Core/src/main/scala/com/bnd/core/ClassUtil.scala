package com.bnd.core

import scala.reflect.ClassTag
import scala.reflect.ClassManifestFactory
import scala.reflect.ManifestFactory
import scala.reflect.runtime.{universe => ru}

object ClassUtil {

    private val mirror = ru.runtimeMirror(this.getClass.getClassLoader)

    def toType[T](clazz : Class[T]) = this.synchronized {mirror.classSymbol(clazz).toType}

    def toManifest[T](clazz : Class[T]) = ManifestFactory.classType[T](clazz)

	def extract[A](implicit m: Manifest[A]): Class[A] = m.erasure.asInstanceOf[Class[A]]    
}