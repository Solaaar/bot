package net.kodehawa.mantarobot.commands;

import br.com.brjdevs.java.utils.extensions.Async;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.kodehawa.mantarobot.commands.rpg.entity.player.EntityPlayer;
import net.kodehawa.mantarobot.commands.rpg.game.ImageGuess;
import net.kodehawa.mantarobot.commands.rpg.game.Pokemon;
import net.kodehawa.mantarobot.commands.rpg.game.Trivia;
import net.kodehawa.mantarobot.modules.Category;
import net.kodehawa.mantarobot.modules.Module;
import net.kodehawa.mantarobot.modules.SimpleCommand;
import net.kodehawa.mantarobot.utils.commands.EmoteReference;

public class GameCmds extends Module {

	public GameCmds() {
		super(Category.GAMES);
		guess();
		trivia();
	}

	private void guess(){
		super.register("guess", new SimpleCommand() {
			@Override
			protected void call(String[] args, String content, GuildMessageReceivedEvent event) {

				if(content.isEmpty()){
					onHelp(event);
					return;
				}

				if(args[0].equals("image")){
					ImageGuess guess = new ImageGuess();
					EntityPlayer player = EntityPlayer.getPlayer(event);
					if(guess.check(event)){
						guess.onStart(event, guess.type(), player);
						/**Async.thread(120000, () -> {
							if(guess.check(event)) return;
							if(EntityPlayer.getPlayer(event.getMember()).getGame() == null) return;
							guess.endGame(event, player, guess, true);
						}).run();**/
					} else {
						event.getChannel().sendMessage(EmoteReference.SAD + "There is someone else playing a game on this channel. Try later or in another one.").queue();
					}

					return;
				}

				if(args[0].equals("pokemon")){
					Pokemon pokemon = new Pokemon();
					EntityPlayer player = EntityPlayer.getPlayer(event);
					if(pokemon.check(event)){
						pokemon.onStart(event, pokemon.type(), player);
						/**Async.thread(120000, () -> {
							if(pokemon.check(event)) return;
							pokemon.endGame(event, player, pokemon, true);
						}).run();**/
					} else {
						event.getChannel().sendMessage(EmoteReference.SAD + "There is someone else playing the a game on this channel. Try later or in another one.").queue();
					}
				}
			}

			@Override
			public MessageEmbed help(GuildMessageReceivedEvent event) {
				return helpEmbed(event, "Guessing games.")
						.addField("Games", "~>guess image: Starts a instance of Guess the image, with anime characters.\n"
								+ "~>guess pokemon: Starts a instance of who's that pokemon?", false)
						.addField("Rules", "You have 10 attempts and 120 seconds to answer, otherwise the game ends", false)
						.build();
			}
		});
	}

	private void trivia() {
		super.register("trivia", new SimpleCommand() {
			@Override
			protected void call(String[] args, String content, GuildMessageReceivedEvent event) {
				Trivia trivia = new Trivia();
				EntityPlayer player = EntityPlayer.getPlayer(event);
				if(trivia.check(event)){
					trivia.onStart(event, trivia.type(), player);
					/**	Async.thread(600000, () -> {
						if(trivia.check(event)) return;
						trivia.endGame(event, player, trivia, true);
					}).run();**/
				} else {
					event.getChannel().sendMessage(EmoteReference.SAD + "There is someone else playing the same game on this channel. Try later or in another one.").queue();
				}
			}

			@Override
			public MessageEmbed help(GuildMessageReceivedEvent event) {
				return helpEmbed(event, "Trivia command.")
						.setDescription("Starts an instance of trivia.")
						.addField("Important", "You need to answer 10 questions correctly to win. You have 600 seconds to answer.", false)
						.build();
			}
		});
	}
}
