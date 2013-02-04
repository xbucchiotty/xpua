package util

trait Writer[T, O] extends (T => O)
