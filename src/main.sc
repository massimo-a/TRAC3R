/*
** Author:  Massimo Angelillo
** A special thank you to Kevin Sangurima for assisstance in
** solving some algorithm issues, naming it and testing it out!
** TRAC3R v0.2.7
*/

import raytracing.{geometry,scene,util},geometry._,scene._,util._;
import annotation.tailrec;

object Test {
	val noise = Noise()
	val hm = HeightMap.generate(1000,1000)((i,j) => noise.layered(i/400.0,j/400.0,5)*300)
	val setup = Scene(spp=1)++(Lighting(x=500,y=1000,z=500,size=50))++(Terrain(hm, 300), Diffuse())
}
object Program {
	//metadata
	private val version = "v0.2.7";
	private val lineStart = "$>";
	private val programName = "TRAC3R";
	
	//pretty println method
	private def p(s: String) {
		println(lineStart + " " + s);
	}
	
	@tailrec private def evaluate(command: String): Boolean = {
		val commands = command.toLowerCase.split(" ");
		try {
			val cmd = commands(0);
			if(Commands.isQuitCommand(cmd)) return false;
			if(Commands.isRenderCommand(cmd)) {
				p("Begun rendering " + commands(1));
				Commands.render(commands(1), Test.setup);
			} else {
				println(Renderer.progressToString);
			}
		} catch {
			case ex: IndexOutOfBoundsException => {
				p("invalid command");
			}
		}
		val nextCommand = scala.io.StdIn.readLine;
		return evaluate(nextCommand);
	}
	def evaluate(): Boolean = {
		val command = scala.io.StdIn.readLine;
		return evaluate(command);
	}
	def main(args: Array[String]): Unit = {
		p(programName + " booted up, " + version);
		evaluate;
		p("=== GOODBYE! ===");
	}
}