package zio.logging

import scala.reflect.ClassTag

/**
 * A `LogAnnotation` describes a particular type of annotation applied to log
 * lines.
 */
final case class LogAnnotation[A: ClassTag](name: String, initialValue: A, combine: (A, A) => A, render: A => String) {
  self =>
  def id: (String, ClassTag[A]) = (name, classTag)

  /**
   * The class tag of the annotation type, used for disambiguation purposes only.
   */
  def classTag: ClassTag[A] = implicitly[ClassTag[A]]

  override def hashCode: Int = id.hashCode

  override def equals(that: Any): Boolean = that match {
    case that: LogAnnotation[_] => self.id == that.id
    case _                      => false
  }

  override def toString: String = "LogAnnotation(" + name + ")"
}

object LogAnnotation {

  /**
   * The `Level` annotation keeps track of log levels.
   */
  val Level = LogAnnotation[LogLevel]("level", LogLevel.Info, (_, r) => r, _.render)

  /**
   * The `Name` annotation keeps track of logger names.
   */
  val Name = LogAnnotation[List[String]]("name", Nil, _ ++ _, _.mkString("."))

  /**
   * The `CorrelationId` annotation keeps track of correlation id.
   */
  val CorrelationId = LogAnnotation[Option[java.util.UUID]](
    name = "correlation-id",
    initialValue = None,
    combine = (_, r) => r,
    render = _.map(_.toString).getOrElse("undefined-correlation-id")
  )
}
