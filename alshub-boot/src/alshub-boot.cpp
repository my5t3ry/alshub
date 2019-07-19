//============================================================================
// Name        : alshub-boot.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>

#include <cstdio>
#include <iostream>
#include <memory>
#include <stdexcept>
#include <string>
#include <array>
#include <iostream>
#include <fstream>
#include <vector>
#include <iterator>
#include <algorithm>
#include <string>
#include <future>
#include <cstring>
#include <sys/socket.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <chrono>
#include <thread>
using namespace std;

using namespace std::this_thread;
// sleep_for, sleep_until
using namespace std::chrono;

std::string
isPortOpen (
  const std::string &domain,
  const std::string &port)
{
  addrinfo *result;
  addrinfo hints
  { };
  hints.ai_family = AF_UNSPEC;   // either IPv4 or IPv6
  hints.ai_socktype = SOCK_STREAM;
  char addressString[INET6_ADDRSTRLEN];
  const char *retval = nullptr;
  if (0
    != getaddrinfo (
      domain.c_str (),
      port.c_str (),
      &hints,
      &result))
  {
    return "";
  }
  for (addrinfo *addr = result; addr != nullptr;
    addr = addr->ai_next)
  {
    int handle = socket (
      addr->ai_family,
      addr->ai_socktype,
      addr->ai_protocol);
    if (handle == -1)
    {
      continue;
    }
    if (connect (handle, addr->ai_addr, addr->ai_addrlen)
      != -1)
    {
      switch (addr->ai_family)
        {
        case AF_INET:
          retval =
            inet_ntop (
              addr->ai_family,
              &(reinterpret_cast<sockaddr_in *> (addr->ai_addr)->sin_addr),
              addressString,
              INET6_ADDRSTRLEN);
          break;
        case AF_INET6:
          retval =
            inet_ntop (
              addr->ai_family,
              &(reinterpret_cast<sockaddr_in6 *> (addr->ai_addr)->sin6_addr),
              addressString,
              INET6_ADDRSTRLEN);
          break;
        default:
          // unknown family
          retval = nullptr;
        }
      close (handle);
      break;
    }
  }
  freeaddrinfo (result);
  return
    retval == nullptr ? "" : domain + ":" + retval + "\n";
}

int
spawnProcess (
  const std::string &cmd,
  const std::string &port)
{

  string data;
  FILE * stream;
  const int max_buffer = 256;
  char buffer[max_buffer];
  stream = popen (cmd.c_str (), "r");
  std::string host = "localhost";
  std::string addr = isPortOpen (host, port);
  while (addr.length () == 0)
  {
    addr = isPortOpen (host, port);
    if (fgets (buffer, max_buffer, stream) != NULL)
      data.append (buffer);
    cout << data << endl;
    sleep_for (milliseconds (100));
  }
//  pclose (stream);
}

int
main ()
{
  cout << "!!!Hello World!!!" << endl; // prints !!!Hello World!!!
  std::string api =
    "/home/my5t3ry/codeBase/java/alshub/alshub-api/target/alshub-api-0.0.1-SNAPSHOT.jar";
  std::string api_port = "8090";
  spawnProcess (api, api_port);
  std::string proxy =
    "/home/my5t3ry/codeBase/java/alshub/alshub-serv/target/alshub-serv-0.0.1-SNAPSHOT.jar";
  std::string proxy_port = "8009";
  spawnProcess (proxy, proxy_port);
  std::string ui =
    "/home/my5t3ry/codeBase/java/alshub/build/test/linux64/test";
  std::string ui_port = "99999";
  spawnProcess (ui, ui_port);

}

