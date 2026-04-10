import scala.io.StdIn.readLine

object main {
  def main(args: Array[String]): Unit = {
    println("Goodbye, World!")
    val greeting = "hello " + signup(args)
    print(greeting)
  }
  def signup(strings: Array[String]):String = {
    if(strings.length > 0)
      strings.head
    else
      readLine("Please enter Name")
  }
}
