
use File::Find;
use File::Copy;

my $totalCount = 0;
my $totalJavaProcessed = 0;

sub processJavaFile #(filename)
{
  my $f = $_[0];
  my $s;
  open (JAVA, "<$f");
  $s = <JAVA>;
  close JAVA;

  if ($s =~ /\/\*/ ) {
    return;
  }

  copy ($f, "$f.bak");
  open (BAK, "<$f.bak");
  open (JAVA, ">$f");

  print JAVA "/*
 * This file is licensed to the Toobs Framework Group under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Toobs Framework Group licenses this file to You under the Apache 
 * License, Version 2.0 (the \"License\"); you may not use this file 
 * except in compliance with the License.  You may obtain a copy of the 
 * License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
";

  while (<BAK>) {
    $s = $_;
    print JAVA $s;
  }
  close BAK;
  close JAVA;
  unlink "$f.bak";
  $totalJavaProcessed++;
}


sub display_files # ()
{
  my $f = $_;
  
  if ($File::Find::dir =~ /\/target\//) {
    return;
  }
  
  if ($f =~ /.*\.java$/) {
    processJavaFile($f);
  } else {
    return;
  }

  $totalCount++;  
}

find (\&display_files, "./");
print "$totalCount files found\n";
print "$totalJavaProcessed java files processed\n";
