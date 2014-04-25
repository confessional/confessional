#!/usr/bin/env ruby

require 'fileutils'
include FileUtils

class Corpus

  def initialize()
    @association_file = 'associations.txt'
    @questions_file = 'questions.txt'
    @sins_file = 'sins.txt'

    @associations = File.open(@association_file).readlines.map { |l|
      ids=l.split.map { |i| i.to_i }
      [ids.shift, ids]
    }
    @sins = File.open(@sins_file).readlines.map { |l|
      parts=l.split
      [parts.shift.to_i, parts.join(' ').capitalize]
    }
    @questions = File.open(@questions_file).readlines.map { |l|
      parts=l.split
      [parts.shift.to_i, parts.join(' ').capitalize]
    }
  end


  def question_by_id(question_id)
    index = @questions.index { |x| x[0] == question_id }
    @questions[index][1] unless index.nil?
  end

  def sin_question_ids(sin_id)
    index=@associations.index { |x| x[0] == sin_id }
    @associations[index][1] unless index.nil?
  end

  def write_associations()
    content = @associations.map { |x| "#{x[0]} #{x[1].join(' ')}" }.join('\n')
    File.open(@association_file, 'w') { |f| f.puts content }
  end

  def run()
    puts "#{@sins.count} sins, #{@questions.count} questions"

    @sins.each { |s|
      puts "-------------------------------------------------"
      id=s[0]
      sin=s[1]
      puts "#{id}. #{sin}"
      q_ids=sin_question_ids(id)
      q_ids.each { |q_id|
        puts "- #{q_id} #{question_by_id(q_id)} YES!"
      } unless q_ids.nil?
      new_q_ids = gets
      index = @associations.index { |x| x[0] == id } || @associations.count
      @associations[index] = [id, new_q_ids.split]
      write_associations()
    }
  end
end

Corpus.new().run()
