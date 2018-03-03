
import java.lang.reflect.Field;
import java.util.ArrayList;


public class Quiz {

	
    public static void main(String[] args) {
	    Quiz quiz = new Quiz();

	    quiz.printQuestions();
	    
	    while(true) {
	    		if(quiz.findAnwser(0)) {
	    			System.out.println("找到答案:");
	    			quiz.printAnwser();
	    			System.out.println();
	    			
//	    			quiz.printReport();
//	    			System.out.println();
	    			
	    			quiz.resetAnwser();
	    		} else {
	    			if(quiz.existAnwsers.isEmpty())
	    				System.out.println("Failed get Answer!!!");
	    			break;
	    		}
	    }
    }
    
    
    private final Question[] questions;
    private char[] curAnwsers = new char[10];
    public ArrayList<char[]> existAnwsers = new ArrayList<>();
    enum AnwserStatus {
        Success,
        Error,
        NotFinish
    }
    
    public void resetAnwser() {
    		curAnwsers = new char[10];
    }
    public Quiz() {
        questions = new Question[10];
        try {
            for (int i = 0; i < 10; i++) {
                Field field = Quiz.class.getField("question" + (i + 1));
                questions[i] = (Question) field.get(this);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void printQuestions() {
		System.out.println("单项选择题：");
		for(int i = 0; i < questions.length; i++) {
			System.out.println(questions[i].getNumber() + "." + questions[i].getDescription());
		}
    }

    public void printAnwser() {
    		for (int j = 0; j < 10; j++) {
            try {
                System.out.print((j + 1) + "." + questions[j].getAnswer() + " ");
            } catch (NoAnswerYet noAnswerYet) {
                noAnswerYet.printStackTrace();
            }
        }
    }
    public void printReport() {
    		for (int j = 0; j < 10; j++) {
            System.out.println(questions[j].report());
            System.out.println();
        }
    }

    public boolean findAnwser(int ques) {
        Question question = questions[ques];
        for (char i = 'A'; i <= 'D' ; i++) {
            question.tryAnwser(i);
            AnwserStatus status = verifyAnwsers();
            if (status == AnwserStatus.Error) {
                question.revertAnwser();
                continue;
            }
            if (status == AnwserStatus.Success) {
            		if(checkIsReplcated()) {
            			return true;
            		} else {
            			question.revertAnwser();
            			return false;
            		}
            } else {
                if(!findAnwser(ques+1)) {
                    question.revertAnwser();
                    continue;
                } else {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean checkIsReplcated() {
    		boolean alreadyExist = false;
		for(int j = 0; j < existAnwsers.size(); j++) {
			char[] anwser = existAnwsers.get(j);
			boolean equal = true;
			for(int k = 0; k < anwser.length; k++) {
				if(anwser[k] != curAnwsers[k]) {
					equal = false;
					break;
				}
			}
			if(equal) {
				alreadyExist = true;
				break;
			}
		}
		if(alreadyExist) {
			return false;
		} else {
			existAnwsers.add(curAnwsers);
			return true;
		}
    }
    private AnwserStatus verifyAnwsers() {
        boolean notFinish = false;
        boolean error = false;
        for (int j = 0; j < questions.length; j++) {
            try {
                if(!questions[j].verify()) {
                    error = true;
                    break;
                }
            } catch (NoAnswerYet noAnswerYet) {
                notFinish = true;
            }
        }
        if (error) {
            return AnwserStatus.Error;
        } else if(notFinish) {
            return AnwserStatus.NotFinish;
        } else {
            return AnwserStatus.Success;
        }
    }

    @SuppressWarnings("serial")
	static class NoAnswerYet extends Exception {
        public NoAnswerYet(int i) {
            super("question " + i + " not answer yet!");
        }
    }

    private abstract class Question {
        public Question(int number) {
            this.number = number;
        }

        private int number;

        public int getNumber() {
            return number;
        }

        public void tryAnwser(char ch) {
            curAnwsers[getNumber()-1] = ch;
        }

        public void revertAnwser() {
            curAnwsers[getNumber()-1] = 0;
        }

        char getAnswer() throws NoAnswerYet {
            if(curAnwsers[number-1] == 0)
                throw new NoAnswerYet(number);
            return curAnwsers[number-1];
        }

        abstract boolean verify() throws NoAnswerYet;
        abstract String report();
        abstract String getDescription();
        
        public int[] getAnswerCount() throws NoAnswerYet {
            int[] counts = new int[4];
            for (int i = 0; i < questions.length; i++) {
                counts[questions[i].getAnswer() - 'A'] ++;
            }
            return counts;
        }
    }

    public Question question1 = new Question(1) {

        @Override
        public String getDescription() {
            return "这道题的答案是：\nA.A B.B C.C D.D\n";
        }

        @Override
        public boolean verify() {
            return true;
        }

        @Override
        public String report() {
            try {
                return getNumber() + "." + getDescription() + "\n" + "答案：" + getAnswer() + "\n";
            } catch (NoAnswerYet noAnswerYet) {
                noAnswerYet.printStackTrace();
                return null;
            }
        }
    };

    public Question question2 = new Question(2) {

        @Override
        public String getDescription() {
            return "第五题的答案是：\nA.C B.D C.A D.B\n";
        }

        @Override
        public boolean verify() throws NoAnswerYet {
            char q5anwser;
            switch (getAnswer()) {
                case 'A':
                    q5anwser = 'C';
                    break;
                case 'B':
                    q5anwser = 'D';
                    break;
                case 'C':
                    q5anwser = 'A';
                    break;
                case 'D':
                    q5anwser = 'B';
                    break;
                default:
                    return false;
            }
            return (question5.getAnswer() == q5anwser);
        }

        @Override
        public String report() {
            try {
                return getNumber() + "." + getDescription() + "\n" + "答案：" + getAnswer() + "\n" + "第5题的答案是" + question5.getAnswer();
            } catch (NoAnswerYet noAnswerYet) {
                noAnswerYet.printStackTrace();
                return null;
            }
        }
    };

    public Question question3 = new Question(3) {

        @Override
        public String getDescription() {
            return "以下选项中哪一题的答案与其他三项不同：\nA.第3题 B.第6题 C.第2题 D.第4题\n";
        }

        @Override
        public boolean verify() throws NoAnswerYet {
            
            char ch = getAnswer();
            switch (ch) {
                case 'A':
                    return (question3.getAnswer() != question6.getAnswer()) && (question6.getAnswer() == question2.getAnswer() && question6.getAnswer() == question4.getAnswer());
                case 'B':
                    return (question6.getAnswer() != question3.getAnswer()) && (question3.getAnswer() == question2.getAnswer() && question3.getAnswer() == question4.getAnswer());
                case 'C':
                    return (question2.getAnswer() != question3.getAnswer()) && (question3.getAnswer() == question6.getAnswer() && question3.getAnswer() == question4.getAnswer());
                case 'D':
                    return (question4.getAnswer() != question3.getAnswer()) && (question3.getAnswer() == question6.getAnswer() && question3.getAnswer() == question2.getAnswer());
            }
            return false;
        }

        @Override
        public String report() {
            try {
                return getNumber() + "." + getDescription() + "\n" + "答案：" + getAnswer() + "\n" + "第3,6,2,4题的答案分别是：" + question3.getAnswer() + "/" + question6.getAnswer()  + "/" + question2.getAnswer()  + "/" + question4.getAnswer();
            } catch (NoAnswerYet noAnswerYet) {
                noAnswerYet.printStackTrace();
                return null;
            }
        }
    };

    public Question question4 = new Question(4) {

        @Override
        public String getDescription() {
            return "以下选项中哪两题的答案相同：\nA.第1，5题 B.第2，7题 C.第1，9题 D.第6，10题\n";
        }


        @Override
        public boolean verify() throws NoAnswerYet {
            char ch = getAnswer();
            switch (ch) {
                case 'A':
                    return (question1.getAnswer() == question5.getAnswer());
                case 'B':
                    return (question2.getAnswer() == question7.getAnswer());
                case 'C':
                    return (question1.getAnswer() == question9.getAnswer());
                case 'D':
                    return (question6.getAnswer() == question10.getAnswer());
            }
            return false;
        }

        @Override
        public String report() {
            try {
                return getNumber() + "." + getDescription() + "\n" + "答案：" + getAnswer() + "\n" + "第1,2,5,6,7,9,10题的答案分别是：" + question1.getAnswer() + "/" + question2.getAnswer()  + "/" + question5.getAnswer()  + "/" + question6.getAnswer() + "/" + question7.getAnswer() + "/" + question9.getAnswer() + "/" + question10.getAnswer();
            } catch (NoAnswerYet noAnswerYet) {
                noAnswerYet.printStackTrace();
                return null;
            }
        }
    };

    public Question question5 = new Question(5) {

        @Override
        public String getDescription() {
            return "以下选项中哪一题的答案与本题相同：\nA.第8题 B.第4题 C.第9题 D.第7题\n";
        }

        @Override
        public boolean verify() throws NoAnswerYet {
            char ch = getAnswer();
            switch (ch) {
                case 'A':
                    return (question8.getAnswer() == ch);
                case 'B':
                    return (question4.getAnswer() == ch);
                case 'C':
                    return (question9.getAnswer() == ch);
                case 'D':
                    return (question7.getAnswer() == ch);
            }
            return false;
        }

        @Override
        public String report() {
            try {
                return getNumber() + "." + getDescription() + "\n" + "答案：" + getAnswer() + "\n" + "第8,4,9,7题的答案分别是：" + question8.getAnswer() + "/" + question4.getAnswer()  + "/" + question9.getAnswer()  + "/" + question7.getAnswer();
            } catch (NoAnswerYet noAnswerYet) {
                noAnswerYet.printStackTrace();
                return null;
            }
        }
    };

    public Question question6 = new Question(6) {
        @Override
        public String getDescription() {
            return "以下选项中哪两题的答案与第8题相同：\nA.第2，4题 B.第1，6题 C.第3，10题 D.第5，9题\n";
        }


        @Override
        public boolean verify() throws NoAnswerYet {
            char ch = getAnswer();
            Question first;
            Question second;
            switch (ch) {
                case 'A':
                    first = question2;
                    second = question4;
                    break;
                case 'B':
                		first = question1;
                    second = question6;
                    break;
                case 'C':
                		first = question3;
                    second = question10;
                    break;
                case 'D':
                		first = question5;
                    second = question9;
                    break;
                default:
                		return false;
            }
            if (first.getAnswer() != second.getAnswer()) {
                return false;
            }
            return (question8.getAnswer() == first.getAnswer());
        }

        @Override
        public String report() {
            try {
                char ch = getAnswer();
                Question first;
                Question second;
                switch (ch) {
                    case 'A':
                        first = question2;
                        second = question4;
                        break;
                    case 'B':
                    		first = question1;
                        second = question6;
                        break;
                    case 'C':
                    		first = question3;
                        second = question10;
                        break;
                    case 'D':
                    		first = question5;
                        second = question9;
                        break;
                    default:
                    		return null;
                }
                return getNumber() + "." + getDescription() + "\n" + "答案：" + getAnswer() + "\n" + "第8," + first.getNumber() + "," + second.getNumber() + "题的答案是：" + question8.getAnswer() + "\n";
            } catch (NoAnswerYet noAnswerYet) {
                noAnswerYet.printStackTrace();
                return null;
            }
        }
    };

    public Question question7 = new Question(7) {
        @Override
        public String getDescription() {
            return "在此十道题中，被选中次数最少的选项字母为：\nA.C B.B C.A D.D\n";
        }


        @Override
        public boolean verify() throws NoAnswerYet {
            int[] counts = getAnswerCount();

            int mini = 0;
            for (int i = 1; i < counts.length; i++) {
                if (counts[mini] > counts[i]) {
                    mini = i;
                }
            }

            //排除掉最小值多于1个的情况
            for (int i = 0; i < counts.length; i++) {
                if(i != mini) {
                    if (counts[i] == counts[mini]) {
                        return false;
                    }
                }
            }

            char ch;
            switch (getAnswer()) {
                case 'A':
                    ch = 'C';
                    break;
                case 'B':
                    ch = 'B';
                    break;
                case 'C':
                    ch = 'A';
                    break;
                case 'D':
                    ch = 'D';
                    break;
                default:
                    return false;
            }
            return ch == ('A' + mini);
        }

        @Override
        public String report() {
            int[] counts = new int[4];
            try {
                for (int i = 0; i < curAnwsers.length; i++) {
                    counts[questions[i].getAnswer() - 'A'] ++;
                }
                return getNumber() + "." + getDescription() + "\n" + "答案：" + getAnswer() + "\n" + "第ABCD的次数分别是：" + counts[0] + "/" + counts[1] + "/" + counts[2]  + "/" + counts[3];
            } catch (NoAnswerYet noAnswerYet) {
                noAnswerYet.printStackTrace();
                return null;
            }
        }
    };

    public Question question8 = new Question(8) {

        @Override
        public String getDescription() {
            return "以下选项中哪一题的答案与第1题的答案在字母中不相邻：\nA.第7题 B.第5题 C.第2题 D.第10题\n";
        }

        @Override
        public boolean verify() throws NoAnswerYet {
            char ch = getAnswer();
            Question question;
            switch (ch) {
                case 'A':
                    question = question7;
                    break;
                case 'B':
                    question = question5;
                    break;
                case 'C':
                    question = question2;
                    break;
                case 'D':
                    question = question10;
                    break;
                default:
                    return false;
            }

            return Math.abs(question.getAnswer() - question1.getAnswer()) != 1;
        }

        @Override
        public String report() {
            try {
                return getNumber() + "." + getDescription() + "\n" + "答案：" + getAnswer() + "\n" + "第1,7,5,2,10题的答案分别是：" + question1.getAnswer() + "/" + question7.getAnswer()  + "/" + question5.getAnswer()  + "/" + question2.getAnswer() + "/" + question10.getAnswer();
            } catch (NoAnswerYet noAnswerYet) {
                noAnswerYet.printStackTrace();
                return null;
            }
        }
    };

    public Question question9 = new Question(9) {
        @Override
        public String getDescription() {
            return "已知\"第1题与第6题的答案相同\"与\"第X题与第五题第答案相同\"的真假性项反：\nA.第6题 B.第10题 C.第2题 D.第9题\n";
        }

        @Override
        public boolean verify() throws NoAnswerYet {
            char ch = getAnswer();
            Question questionx;
            switch (ch) {
                case 'A':
                    questionx = question6;
                    break;
                case 'B':
                    questionx = question10;
                    break;
                case 'C':
                    questionx = question2;
                    break;
                case 'D':
                    questionx = question9;
                    break;
                default:
                    return false;
            }

            return (question1.getAnswer() != question6.getAnswer()) == (questionx.getAnswer() == question5.getAnswer());
        }

        @Override
        public String report() {
            try {
                char ch = getAnswer();
                Question questionx;
                switch (ch) {
                case 'A':
                    questionx = question6;
                    break;
                case 'B':
                    questionx = question10;
                    break;
                case 'C':
                    questionx = question2;
                    break;
                case 'D':
                    questionx = question9;
                        break;
                    default:
                        return null;
                }
                return getNumber() + "." + getDescription() + "\n" + "答案：" + getAnswer() + "\n" + "第1,6," + questionx.getNumber() + ",5答案分别是" + question1.getAnswer() + "/" + question6.getAnswer() + "/" + questionx.getAnswer() + "/" + question5.getAnswer();
            } catch (NoAnswerYet noAnswerYet) {
                noAnswerYet.printStackTrace();
                return null;
            }
        }
    };

    public Question question10 = new Question(10) {
        @Override
        public String getDescription() {
            return "此10道题中，ABCD四个字母出现次数最多与最少的差为：\nA.3 B.2 C.4 D.1\n";
        }


        @Override
        public boolean verify() throws NoAnswerYet {
            int[] counts = getAnswerCount();

            int mini = 0;
            int max = 0;
            for (int i = 1; i < counts.length; i++) {
                if (counts[mini] > counts[i]) {
                    mini = i;
                }
                if (counts[max] < counts[i]) {
                    max = i;
                }
            }

            char ch = getAnswer();
            int value;
            switch (ch) {
                case 'A':
                    value = 3;
                    break;
                case 'B':
                    value = 2;
                    break;
                case 'C':
                    value = 4;
                    break;
                case 'D':
                    value = 1;
                    break;
                default:
                    return false;
            }

            return (counts[max] - counts[mini]) == value;
        }

        @Override
        public String report() {            
            try {
            		int[] counts = getAnswerCount();
                return getNumber() + "." + getDescription() + "\n" + "答案：" + getAnswer() + "\n" + "ABCD的次数分别是：" + counts[0] + "/" + counts[1] + "/" + counts[2]  + "/" + counts[3];
            } catch (NoAnswerYet noAnswerYet) {
                noAnswerYet.printStackTrace();
                return null;
            }
        }
    };
}
